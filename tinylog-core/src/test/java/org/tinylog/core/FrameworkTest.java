package org.tinylog.core;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tinylog
class FrameworkTest {

    @Inject
    private InternalLogger logger;

    /**
     * Initializes the logging backend mock.
     */
    @BeforeEach
    void init() {
        TestLoggingBackendBuilder.backend = mock(LoggingBackend.class);
    }

    /**
     * Tests for {@link Framework#getInternalLogger()}.
     */
    @Nested
    class InternalLoggerGetter {

        /**
         * Verifies that an {@link InternalLogger} instance is provided.
         */
        @Test
        void receive() {
            InternalLogger logger = new Framework().getInternalLogger();
            assertThat(logger).isNotNull();
        }

    }

    /**
     * Tests for {@link Framework#getClassLoader()}.
     */
    @Nested
    class ClassLoaderGetter {

        /**
         * Verifies that {@link Thread#getContextClassLoader()} is provided by default.
         */
        @Test
        void receiveContextClassLoader() {
            ClassLoader classLoader = new Framework().getClassLoader();
            assertThat(classLoader).isSameAs(Thread.currentThread().getContextClassLoader());
        }

        /**
         * Verifies that {@link Class#getClassLoader()} is provided as fallback.
         */
        @Test
        void receiveFrameworkClassLoader() {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(null);

            try {
                ClassLoader classLoader = new Framework().getClassLoader();
                assertThat(classLoader).isSameAs(Framework.class.getClassLoader());
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        }

    }

    /**
     * Tests for {@link Framework#getRuntime()}.
     */
    @Nested
    class RuntimeGetter {

        /**
         * Verifies that a supported runtime flavor is provided.
         */
        @Test
        void receive() {
            RuntimeFlavor runtime = new Framework().getRuntime();
            assertThat(runtime).isNotNull();
        }

    }

    /**
     * Tests for {@link Framework#getContextStorage()}.
     */
    @Nested
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
    class ContextStorageGetter {

        /**
         * Verifies that the context storage can be received if the framework is not started yet.
         */
        @Test
        void getBeforeStarted() throws InterruptedException {
            ContextStorage storage = mock(ContextStorage.class);
            when(TestLoggingBackendBuilder.backend.getContextStorage()).thenReturn(storage);

            Framework framework = new Framework();
            try {
                assertThat(framework.getContextStorage()).isSameAs(storage);
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that the context storage can be received if the framework is already started.
         */
        @Test
        void getAfterStarted() throws InterruptedException {
            ContextStorage storage = mock(ContextStorage.class);
            when(TestLoggingBackendBuilder.backend.getContextStorage()).thenReturn(storage);

            Framework framework = new Framework();
            framework.start();
            try {
                assertThat(framework.getContextStorage()).isSameAs(storage);
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Tests for {@link Framework#getLevelVisibilityByClass(String)}.
     */
    @Nested
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
    class LevelVisibilityByClassNameGetter {

        /**
         * Verifies that the level visibility of a class name can be received if the framework is not started yet.
         */
        @Test
        void getBeforeStarted() throws InterruptedException {
            LevelVisibility visibility = mock(LevelVisibility.class);
            when(TestLoggingBackendBuilder.backend.getLevelVisibilityByClass("example.Foo")).thenReturn(visibility);

            Framework framework = new Framework();
            try {
                assertThat(framework.getLevelVisibilityByClass("example.Foo")).isEqualTo(visibility);
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that the level visibility of a class name can be received if the framework is already started.
         */
        @Test
        void getAfterStarted() throws InterruptedException {
            LevelVisibility visibility = mock(LevelVisibility.class);
            when(TestLoggingBackendBuilder.backend.getLevelVisibilityByClass("example.Foo")).thenReturn(visibility);

            Framework framework = new Framework();
            framework.start();
            try {
                assertThat(framework.getLevelVisibilityByClass("example.Foo")).isEqualTo(visibility);
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Tests for {@link Framework#getLevelVisibilityByTag(String)}.
     */
    @Nested
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
    class LevelVisibilityByTagGetter {

        /**
         * Verifies that the level visibility of a tag can be received if the framework is not started yet.
         */
        @Test
        void getBeforeStarted() throws InterruptedException {
            LevelVisibility visibility = mock(LevelVisibility.class);
            when(TestLoggingBackendBuilder.backend.getLevelVisibilityByTag("foo")).thenReturn(visibility);

            Framework framework = new Framework();
            try {
                assertThat(framework.getLevelVisibilityByTag("foo")).isEqualTo(visibility);
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that the level visibility of a tag can be received if the framework is already started.
         */
        @Test
        void getAfterStarted() throws InterruptedException {
            LevelVisibility visibility = mock(LevelVisibility.class);
            when(TestLoggingBackendBuilder.backend.getLevelVisibilityByTag("foo")).thenReturn(visibility);

            Framework framework = new Framework();
            framework.start();
            try {
                assertThat(framework.getLevelVisibilityByTag("foo")).isEqualTo(visibility);
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Tests for {@link Framework#isEnabled(Object, String, Level)}.
     */
    @Nested
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
    class SeverityLevelStateGetter {

        /**
         * Verifies that the enabled state of a severity can be received if the framework is not started yet.
         *
         * @param result The enabled state to return
         */
        @ParameterizedTest
        @ValueSource(booleans = {false, true})
        void getBeforeStarted(boolean result) throws InterruptedException {
            when(TestLoggingBackendBuilder.backend.isEnabled("MyClass", "foo", Level.INFO)).thenReturn(result);

            Framework framework = new Framework();
            try {
                assertThat(framework.isEnabled("MyClass", "foo", Level.INFO)).isEqualTo(result);
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that the enabled state of a severity can be received if the framework is already started.
         *
         * @param result The enabled state to return
         */
        @ParameterizedTest
        @ValueSource(booleans = {false, true})
        void getAfterStarted(boolean result) throws InterruptedException {
            when(TestLoggingBackendBuilder.backend.isEnabled("MyClass", "foo", Level.INFO)).thenReturn(result);

            Framework framework = new Framework();
            framework.start();
            try {
                assertThat(framework.isEnabled("MyClass", "foo", Level.INFO)).isEqualTo(result);
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Tests for {@link Framework#submit(LogEntry)}.
     */
    @Nested
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
    class LogEntrySubmission {

        /**
         * Verifies that a log entry can be submitted before the framework is initialized.
         */
        @Test
        void submitBeforeStarted() throws InterruptedException {
            LogEntry entry = mock(LogEntry.class);

            Framework framework = new Framework();
            framework.submit(entry);
            framework.start();

            try {
                await().untilAsserted(
                    () -> verify(TestLoggingBackendBuilder.backend).output(same(entry), anyBoolean())
                );
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that a log entry can be submitted after the framework is initialized.
         */
        @Test
        void submitAfterStarted() throws InterruptedException {
            Framework framework = new Framework();
            framework.start();

            try {
                LogEntry entry = mock(LogEntry.class);
                framework.submit(entry);

                await().untilAsserted(
                    () -> verify(TestLoggingBackendBuilder.backend).output(same(entry), anyBoolean())
                );
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Verifies that the initial configuration is loaded from available {@link ConfigurationLoader} implementations.
     */
    @Nested
    class InitialConfiguration {

        /**
         * Verifies that the only available configuration loader is used.
         */
        @RegisterService(service = ConfigurationLoader.class, implementations = FirstConfigurationLoader.class)
        @Test
        void singleConfigurationLoader() throws InterruptedException {
            Framework framework = new Framework();
            try {
                ConfigurationBuilder builder = framework.getConfigurationBuilder(true);
                assertThat(builder.get("foo")).isEqualTo("1");
                assertThat(builder.get("bar")).isEqualTo("1");
                assertThat(builder.get("baz")).isNull();
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that only the configuration loader with the highest priority is used.
         */
        @RegisterService(
            service = ConfigurationLoader.class,
            implementations = {FirstConfigurationLoader.class, SecondConfigurationLoader.class}
        )
        @Test
        void multipleConfigurationLoaders() throws InterruptedException {
            Framework framework = new Framework();
            try {
                ConfigurationBuilder builder = framework.getConfigurationBuilder(true);
                assertThat(builder.get("foo")).isEqualTo("2");
                assertThat(builder.get("bar")).isNull();
                assertThat(builder.get("baz")).isEqualTo("2");
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Tests for {@link Framework#getConfigurationBuilder(boolean)}.
     */
    @Nested
    class ConfigurationBuilderGetter {

        /**
         * Verifies that an empty configuration builder can be received.
         */
        @Test
        void receiveEmptyConfigurationBuilder() throws InterruptedException {
            Framework framework = new Framework();
            try {
                Configuration configuration = new Configuration(Map.of("foo", "bar"), logger);
                framework.setConfiguration(configuration);

                ConfigurationBuilder builder = framework.getConfigurationBuilder(false);
                assertThat(builder.get("foo")).isNull();
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that an inherited configuration builder can be received.
         */
        @Test
        void receiveInheritedConfigurationBuilder() throws InterruptedException {
            Framework framework = new Framework();
            try {
                Configuration configuration = new Configuration(Map.of("foo", "bar"), logger);
                framework.setConfiguration(configuration);

                ConfigurationBuilder builder = framework.getConfigurationBuilder(true);
                assertThat(builder.get("foo")).isEqualTo("bar");
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Tests for {@link Framework#setConfiguration(Configuration)}.
     */
    @Nested
    class ConfigurationSetter {

        /**
         * Verifies that an initial configuration can be set and applied before the framework is initialized.
         */
        @Test
        @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
        void setInitialConfigurationBeforeStarted() throws InterruptedException {
            ContextStorage storage = mock(ContextStorage.class);
            when(TestLoggingBackendBuilder.backend.getContextStorage()).thenReturn(storage);

            Framework framework = new Framework();
            framework.setConfiguration(new Configuration(Map.of("backends", "test1"), logger));

            try {
                assertThat(framework.getContextStorage()).isSameAs(storage);
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that configurations can be overridden before the framework is initialized.
         */
        @Test
        @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
        void overrideExistingConfigurationBeforeStarted() throws InterruptedException {
            ContextStorage storage = mock(ContextStorage.class);
            when(TestLoggingBackendBuilder.backend.getContextStorage()).thenReturn(storage);

            Framework framework = new Framework();
            framework.setConfiguration(new Configuration(Map.of("backends", "nop"), logger));
            framework.setConfiguration(new Configuration(Map.of("backends", "test1"), logger));

            try {
                assertThat(framework.getContextStorage()).isSameAs(storage);
            } finally {
                framework.stop();
            }
        }

        /**
         * Verifies that configurations cannot be overridden after the framework is initialized.
         */
        @Test
        @RegisterService(service = LoggingBackendBuilder.class, implementations = TestLoggingBackendBuilder.class)
        void preventOverridingConfigurationAfterStarted() throws InterruptedException {
            ContextStorage storage = mock(ContextStorage.class);
            when(TestLoggingBackendBuilder.backend.getContextStorage()).thenReturn(storage);

            Framework framework = new Framework();
            framework.start();

            try {
                Configuration configuration = new Configuration(Map.of("backends", "test1"), logger);
                assertThatCode(() -> framework.setConfiguration(configuration))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("configuration");
            } finally {
                framework.stop();
            }
        }

    }

    /**
     * Logging backend builder with a mocked backend for JUnit tests.
     */
    public static final class TestLoggingBackendBuilder implements LoggingBackendBuilder {

        private static LoggingBackend backend;

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public LoggingBackend create(TinylogContext context) {
            return backend;
        }

    }

    /**
     * First configuration loader service implementation.
     */
    public static final class FirstConfigurationLoader implements ConfigurationLoader {

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public Map<String, String> load(ClassLoader loader, InternalLogger logger) {
            return Map.of("foo", "1", "bar", "1");
        }

    }

    /**
     * Second configuration loader service implementation.
     */
    public static final class SecondConfigurationLoader implements ConfigurationLoader {

        @Override
        public int getPriority() {
            return 2;
        }

        @Override
        public Map<String, String> load(ClassLoader loader, InternalLogger logger) {
            return Map.of("foo", "2", "baz", "2");
        }

    }

}
