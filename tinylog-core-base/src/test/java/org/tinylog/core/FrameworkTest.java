package org.tinylog.core;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.tinylog.core.backend.BundleLoggingBackend;
import org.tinylog.core.backend.InternalLoggingBackend;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.NopLoggingBackendBuilder;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.test.service.RegisterService;
import org.tinylog.core.test.system.CaptureSystemOutput;
import org.tinylog.core.test.system.Output;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@CaptureSystemOutput(excludes = "TINYLOG WARN:.*tinylog-impl\\.jar.*")
class FrameworkTest {

    @Inject
    private Output output;

    /**
     * Verifies that a {@link RuntimeFlavor} is provided.
     */
    @Test
    void runtime() {
        assertThat(new Framework(false, false).getRuntime()).isNotNull();
    }

    /**
     * Verifies that a working {@link Clock} with the default time zone is provided.
     */
    @Test
    void clock() {
        Clock clock = new Framework(false, false).getClock();
        assertThat(clock.getZone()).isEqualTo(ZoneId.systemDefault());

        Instant before = Clock.systemDefaultZone().instant();
        Instant instant = clock.instant();
        Instant after = Clock.systemDefaultZone().instant();
        assertThat(instant).isBetween(before, after);
    }

    /**
     * Tests for receiving, modifying, and applying configurations.
     */
    @Nested
    class Configurations {

        /**
         * Verifies that no configuration loader will be used if configuration loading is disabled.
         */
        @RegisterService(
            service = ConfigurationLoader.class,
            implementations = TestOneConfigurationLoader.class
        )
        @Test
        void useEmptyConfiguration() {
            TestOneConfigurationLoader.data = singletonMap("foo", "bar");

            Framework framework = new Framework(false, false);
            Configuration configuration = framework.getConfiguration();

            assertThat(configuration.getAllValues()).isEmpty();
        }

        /**
         * Verifies that the configuration loader with the highest priority will be used.
         */
        @RegisterService(
            service = ConfigurationLoader.class,
            implementations = {TestOneConfigurationLoader.class, TestTwoConfigurationLoader.class}
        )
        @Test
        void useConfigurationLoaderWithHighestPriority() {
            TestOneConfigurationLoader.data = singletonMap("first", "yes");
            TestTwoConfigurationLoader.data = singletonMap("second", "yes");

            Framework framework = new Framework(true, false);
            Configuration configuration = framework.getConfiguration();

            assertThat(configuration.getValue("first")).isNull();
            assertThat(configuration.getValue("second")).isEqualTo("yes");
        }

        /**
         * Verifies that a configuration loader that cannot provide a configuration is skipped.
         */
        @RegisterService(
            service = ConfigurationLoader.class,
            implementations = {TestOneConfigurationLoader.class, TestTwoConfigurationLoader.class}
        )
        @Test
        void skipConfigurationLoaderWithoutResult() {
            TestOneConfigurationLoader.data = singletonMap("first", "yes");
            TestTwoConfigurationLoader.data = null;

            Framework framework = new Framework(true, false);
            Configuration configuration = framework.getConfiguration();

            assertThat(configuration.getValue("first")).isEqualTo("yes");
            assertThat(configuration.getValue("second")).isNull();
        }

        /**
         * Verifies that an empty configuration builder can be received without inheriting the existing configuration.
         */
        @Test
        void receiveEmptyConfigurationBuilder() {
            Framework framework = new Framework(false, false);
            framework.getConfigurationBuilder(false).set("foo", "bar").activate();

            ConfigurationBuilder builder = framework.getConfigurationBuilder(false);
            assertThat(builder.get("foo")).isNull();
        }

        /**
         * Verifies that an inherit configuration builder can be received that contains the existing configuration.
         */
        @Test
        void receiveInheritedConfigurationBuilder() {
            Framework framework = new Framework(false, false);
            framework.getConfigurationBuilder(false).set("foo", "bar").activate();

            ConfigurationBuilder builder = framework.getConfigurationBuilder(true);
            assertThat(builder.get("foo")).isEqualTo("bar");
        }

    }

    /**
     * Tests for {@link Framework#getClassLoader()}.
     */
    @Nested
    class ClassLoaderGetter {

        /**
         * Verifies that the context class loader from the current thread will be used if available.
         */
        @Test
        void provideFromCurrentThread() {
            ClassLoader classLoader = new Framework(false, false).getClassLoader();
            assertThat(classLoader).isNotNull().isEqualTo(Thread.currentThread().getContextClassLoader());
        }

        /**
         * Verifies that the class loader from {@link Framework} class will be used, if the context class loader from
         * the current thread is unavailable.
         */
        @Test
        void provideFromClass() {
            Thread thread = Thread.currentThread();
            ClassLoader threadClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(null);
                ClassLoader providedClassLoader = new Framework(false, false).getClassLoader();
                assertThat(providedClassLoader).isNotNull().isEqualTo(Framework.class.getClassLoader());
            } finally {
                thread.setContextClassLoader(threadClassLoader);
            }
        }

    }

    /**
     * Tests for {@link Framework#registerHook(Hook)}, {@link Framework#removeHook(Hook)}, {@link Framework#startUp()},
     * and {@link Framework#shutDown()}.
     */
    @Nested
    class LifeCycle {

        /**
         * Verifies that as service registered hooks are loaded.
         */
        @RegisterService(service = Hook.class, implementations = TestHook.class)
        @Test
        void loadServiceHooks() {
            Framework framework = new Framework(false, true);
            assertThat(TestHook.running).isFalse();

            try {
                framework.startUp();
                assertThat(TestHook.running).isTrue();
            } finally {
                framework.shutDown();
                assertThat(TestHook.running).isFalse();
            }
        }

        /**
         * Verifies that a hook, registered before the framework start up, will be correctly started and shut down.
         */
        @Test
        void registerHookBeforeStartUp() {
            Hook hook = mock(Hook.class);
            Framework framework = new Framework(false, false);

            framework.registerHook(hook);
            verify(hook, never()).startUp();

            try {
                framework.startUp();
                verify(hook).startUp();
            } finally {
                framework.shutDown();
                verify(hook).shutDown();
            }
        }

        /**
         * Verifies that a hook, registered after the framework start up, will be correctly started and shut down.
         */
        @Test
        void registerHookAfterStartUp() {
            Hook hook = mock(Hook.class);
            Framework framework = new Framework(false, false);

            try {
                framework.startUp();
                framework.registerHook(hook);
                verify(hook).startUp();
            } finally {
                framework.shutDown();
                verify(hook).shutDown();
            }
        }

        /**
         * Verifies that hooks are called only called during the first startup.
         */
        @Test
        void ignoreSecondStartup() {
            Hook hook = mock(Hook.class);
            Framework framework = new Framework(false, false);
            framework.registerHook(hook);

            try {
                framework.startUp();
                verify(hook).startUp();
                clearInvocations(hook);

                framework.startUp();
                verify(hook, never()).startUp();
            } finally {
                framework.shutDown();
            }
        }

        /**
         * Verifies that hooks are called only called during the first shutdown.
         */
        @Test
        void ignoreSecondShutdown() {
            Hook hook = mock(Hook.class);
            Framework framework = new Framework(false, false);
            framework.registerHook(hook);

            try {
                framework.startUp();
            } finally {
                framework.shutDown();
                verify(hook).shutDown();
                clearInvocations(hook);

                framework.shutDown();
                verify(hook, never()).shutDown();
            }
        }

        /**
         * Verifies that a removed hook will be not called while shutting the framework down.
         */
        @Test
        void removeHookBeforeShutdown() {
            Hook hook = mock(Hook.class);
            Framework framework = new Framework(false, false);
            framework.registerHook(hook);

            try {
                framework.startUp();
                framework.removeHook(hook);
            } finally {
                framework.shutDown();
            }

            verify(hook).startUp();
            verify(hook, never()).shutDown();
        }

        /**
         * Verifies that the configuration becomes frozen after startup.
         */
        @Test
        void freezeConfigurationAfterStartup() {
            Framework framework = new Framework(false, false);
            try {
                framework.startUp();
                assertThatCode(() -> framework.setConfiguration(new Configuration(emptyMap())))
                    .isInstanceOf(UnsupportedOperationException.class);
            } finally {
                framework.shutDown();
            }
        }

        /**
         * Verifies that the internal logger is initialized after startup.
         */
        @Test
        void initializeInternalLoggerAfterStartup() {
            Framework framework = new Framework(false, false);
            try {
                framework.startUp();
                InternalLogger.warn(null, "Hello World!");
                assertThat(output.consume()).containsExactly("TINYLOG WARN: Hello World!");
            } finally {
                framework.shutDown();
            }
        }

    }

    /**
     * Tests for {@link Framework#getLoggingBackend()}.
     */
    @Nested
    class LoggingBackendGetter {

        /**
         * Verifies that the internal logging backend is loaded if none other is available.
         */
        @CaptureSystemOutput // Reset the default excludes
        @Test
        void loadInternalLoggingBackend() {
            Framework framework = new Framework(false, false);
            assertThat(framework.getLoggingBackend()).isInstanceOf(InternalLoggingBackend.class);
            assertThat(output.consume())
                .hasSize(1)
                .allSatisfy(line -> assertThat(line).contains("TINYLOG WARN", "tinylog-impl.jar"));
        }

        /**
         * Verifies that a logging backend is loaded if it is the only available.
         */
        @RegisterService(service = LoggingBackendBuilder.class, implementations = TestOneLoggingBackendBuilder.class)
        @Test
        void loadSingleAvailableProvider() {
            Framework framework = new Framework(false, false);
            assertThat(framework.getLoggingBackend()).isSameAs(TestOneLoggingBackendBuilder.backend);
        }

        /**
         * Verifies that all available logging backends are loaded and bundled in a {@link BundleLoggingBackend}.
         */
        @RegisterService(
            service = LoggingBackendBuilder.class,
            implementations = {TestOneLoggingBackendBuilder.class, TestTwoLoggingBackendBuilder.class}
        )
        @Test
        void loadAllAvailableProviders() {
            Framework framework = new Framework(false, false);

            LoggingBackend backend = framework.getLoggingBackend();
            assertThat(backend).isInstanceOf(BundleLoggingBackend.class);

            Collection<LoggingBackend> children = ((BundleLoggingBackend) backend).getChildren();
            assertThat(children).containsExactlyInAnyOrder(
                TestOneLoggingBackendBuilder.backend, TestTwoLoggingBackendBuilder.backend
            );
        }

        /**
         * Verifies that one logging backend can be defined by name if multiple are available.
         */
        @RegisterService(
            service = LoggingBackendBuilder.class,
            implementations = {TestOneLoggingBackendBuilder.class, TestTwoLoggingBackendBuilder.class}
        )
        @Test
        void loadSingleProviderByName() {
            Framework framework = new Framework(false, false);
            framework.getConfigurationBuilder(false)
                .set("backends", "test2")
                .activate();

            assertThat(framework.getLoggingBackend()).isSameAs(TestTwoLoggingBackendBuilder.backend);
        }

        /**
         * Verifies that several logging backends can be defined by name if multiple are available.
         */
        @RegisterService(
            service = LoggingBackendBuilder.class,
            implementations = {TestOneLoggingBackendBuilder.class, TestTwoLoggingBackendBuilder.class}
        )
        @Test
        void loadMultipleProvidersByName() {
            Framework framework = new Framework(true, false);
            framework.getConfigurationBuilder(false)
                .set("backends", "test1, nop")
                .activate();

            LoggingBackend backend = framework.getLoggingBackend();
            assertThat(backend).isInstanceOf(BundleLoggingBackend.class);

            Collection<LoggingBackend> children = ((BundleLoggingBackend) backend).getChildren();
            assertThat(children).containsExactlyInAnyOrder(
                TestOneLoggingBackendBuilder.backend,
                new NopLoggingBackendBuilder().create(null)
            );
        }

        /**
         * Verifies that the available logging backend will be created, if the configured logging backend does not
         * exist.
         */
        @RegisterService(service = LoggingBackendBuilder.class, implementations = TestOneLoggingBackendBuilder.class)
        @Test
        void fallbackForEntireInvalidName() {
            Framework framework = new Framework(true, false);
            framework.getConfigurationBuilder(false)
                .set("backends", "test0")
                .activate();

            LoggingBackend backend = framework.getLoggingBackend();

            assertThat(backend).isSameAs(TestOneLoggingBackendBuilder.backend);
            assertThat(output.consume())
                .hasSize(1)
                .allSatisfy(line -> assertThat(line).contains("TINYLOG ERROR", "test0"));
        }

        /**
         * Verifies that all other configured logging backends will be created, if one of them does not exist.
         */
        @RegisterService(
            service = LoggingBackendBuilder.class,
            implementations = {TestOneLoggingBackendBuilder.class, TestTwoLoggingBackendBuilder.class}
        )
        @Test
        void fallbackForPartialInvalidName() {
            Framework framework = new Framework(true, false);
            framework.getConfigurationBuilder(false)
                .set("backends", "test2, test3")
                .activate();

            LoggingBackend backend = framework.getLoggingBackend();

            assertThat(backend).isSameAs(TestTwoLoggingBackendBuilder.backend);
            assertThat(output.consume())
                .hasSize(1)
                .allSatisfy(line -> assertThat(line).contains("TINYLOG ERROR", "test3"));
        }

        /**
         * Verifies that the configuration becomes frozen after providing a logging backend.
         */
        @Test
        void freezeConfigurationAfterProvidingLoggingBackend() {
            Framework framework = new Framework(false, false);

            assertThat(framework.getLoggingBackend()).isNotNull();
            assertThatCode(() -> framework.setConfiguration(new Configuration(emptyMap())))
                .isInstanceOf(UnsupportedOperationException.class);
        }

    }

    /**
     * Tests for shutting down {@link Framework} via shutdown hook.
     */
    @Nested
    @DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    class AutoShutdown {

        private MockedStatic<Runtime> runtimeClassMock;
        private Runtime runtimeInstanceMock;

        /**
         * Initializes the runtime mocks.
         */
        @SuppressWarnings("ResultOfMethodCallIgnored")
        @BeforeEach
        void init() {
            runtimeClassMock = mockStatic(Runtime.class);
            runtimeInstanceMock = mock(Runtime.class);
            runtimeClassMock.when(Runtime::getRuntime).thenReturn(runtimeInstanceMock);
        }

        /**
         * Closes the static runtime class mock.
         */
        @AfterEach
        void dispose() {
            runtimeClassMock.close();
        }

        /**
         * Verifies that a shutdown hook will be registered, if auto-shutdown is enabled.
         *
         * @param value The value for the {@code auto-shutdown} property
         */
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = { "true", "TRUE" })
        void enableAutoShutdown(String value) {
            Framework framework = new Framework(false, false);
            framework.getConfigurationBuilder(false)
                .set("auto-shutdown", value)
                .activate();

            framework.startUp();
            framework.shutDown();

            verify(runtimeInstanceMock).addShutdownHook(notNull());
        }

        /**
         * Verifies that no shutdown hook will be registered, if auto-shutdown is disabled.
         *
         * @param value The value for the {@code auto-shutdown} property
         */
        @ParameterizedTest
        @ValueSource(strings = { "false", "FALSE" })
        void disableAutoShutdown(String value) {
            Framework framework = new Framework(false, false);
            framework.getConfigurationBuilder(false)
                .set("auto-shutdown", value)
                .activate();

            framework.startUp();
            framework.shutDown();

            verify(runtimeInstanceMock, never()).addShutdownHook(any());
        }

    }

    /**
     * Additional hook for JUnit tests.
     */
    public static final class TestHook implements Hook {

        private static boolean running;

        @Override
        public void startUp() {
            running = true;
        }

        @Override
        public void shutDown() {
            running = false;
        }

    }

    /**
     * Additional logging configuration builder for JUnit tests.
     */
    public static final class TestOneConfigurationLoader implements ConfigurationLoader {

        private static Map<String, String> data;

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public Map<String, String> load(ClassLoader loader) {
            return data;
        }

    }

    /**
     * Additional logging configuration builder for JUnit tests.
     */
    public static final class TestTwoConfigurationLoader implements ConfigurationLoader {

        private static Map<String, String> data;

        @Override
        public int getPriority() {
            return 2;
        }

        @Override
        public Map<String, String> load(ClassLoader loader) {
            return data;
        }

    }

    /**
     * Additional logging backend builder for JUnit tests.
     */
    public static final class TestOneLoggingBackendBuilder implements LoggingBackendBuilder {

        private static final LoggingBackend backend = new InternalLoggingBackend();

        @Override
        public String getName() {
            return "test1";
        }

        @Override
        public LoggingBackend create(Framework framework) {
            return backend;
        }

    }

    /**
     * Additional logging backend builder for JUnit tests.
     */
    public static final class TestTwoLoggingBackendBuilder implements LoggingBackendBuilder {

        private static final LoggingBackend backend = new InternalLoggingBackend();

        @Override
        public String getName() {
            return "test2";
        }

        @Override
        public LoggingBackend create(Framework framework) {
            return backend;
        }

    }

}
