package org.tinylog.core.backend;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Tinylog
class LoggingBackendFactoryTest {

    @Inject
    private RuntimeFlavor runtime;

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

    /**
     * Resets all logging backend mocks.
     */
    @AfterEach
    void reset() {
        Mockito.reset(
            TestOneLoggingBackendBuilder.backend,
            TestTwoLoggingBackendBuilder.backend
        );
    }

    /**
     * Verifies that the internal logging backend is loaded if none other is available.
     */
    @Test
    void loadInternalLoggingBackend() {
        LoggingBackend backend = createLoggingBackend(emptyMap());

        assertThat(backend).isInstanceOf(InternalLoggingBackend.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("tinylog-impl.jar");
        });
    }

    /**
     * Verifies that a logging backend is loaded if it is the only one available.
     */
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestOneLoggingBackendBuilder.class)
    @Test
    void loadSingleAvailableProvider() {
        LoggingBackend backend = createLoggingBackend(emptyMap());
        assertThat(backend).isSameAs(TestOneLoggingBackendBuilder.backend);
    }

    /**
     * Verifies that all available logging backends are loaded and bundled.
     */
    @RegisterService(
        service = LoggingBackendBuilder.class,
        implementations = {TestOneLoggingBackendBuilder.class, TestTwoLoggingBackendBuilder.class}
    )
    @Test
    void loadAllAvailableProviders() {
        LoggingBackend backend = createLoggingBackend(emptyMap());
        backend.output(mock(LogEntry.class), true);

        verify(TestOneLoggingBackendBuilder.backend).output(any(), eq(true));
        verify(TestTwoLoggingBackendBuilder.backend).output(any(), eq(true));
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
        LoggingBackend backend = createLoggingBackend(Map.of("backends", "test2"));
        assertThat(backend).isSameAs(TestTwoLoggingBackendBuilder.backend);
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
        LoggingBackend backend = createLoggingBackend(Map.of("backends", "test1, test2"));
        backend.output(mock(LogEntry.class), true);

        InOrder order = inOrder(TestOneLoggingBackendBuilder.backend, TestTwoLoggingBackendBuilder.backend);
        order.verify(TestOneLoggingBackendBuilder.backend).output(any(), eq(true));
        order.verify(TestTwoLoggingBackendBuilder.backend).output(any(), eq(true));
    }

    /**
     * Verifies that logging backends will be created only once, even if multiple times declared.
     */
    @RegisterService(
        service = LoggingBackendBuilder.class,
        implementations = {TestOneLoggingBackendBuilder.class, TestTwoLoggingBackendBuilder.class}
    )
    @Test
    void loadSameProvidersByName() {
        LoggingBackend backend = createLoggingBackend(Map.of("backends", "test1, test1"));
        assertThat(backend).isSameAs(TestOneLoggingBackendBuilder.backend);
    }

    /**
     * Verifies that the available logging backend will be created, if the configured logging backend does not
     * exist.
     */
    @RegisterService(service = LoggingBackendBuilder.class, implementations = TestOneLoggingBackendBuilder.class)
    @Test
    void fallbackForEntireInvalidName() {
        LoggingBackend backend = createLoggingBackend(Map.of("backends", "test0"));

        assertThat(backend).isSameAs(TestOneLoggingBackendBuilder.backend);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("test0");
        });
    }

    /**
     * Verifies that all other configured logging backends will be created, if one of them does not exist.
     */
    @RegisterService(
        service = LoggingBackendBuilder.class,
        implementations = {TestOneLoggingBackendBuilder.class, TestOneLoggingBackendBuilder.class}
    )
    @Test
    void fallbackForPartialInvalidName() {
        LoggingBackend backend = createLoggingBackend(Map.of("backends", "test0, test1"));

        assertThat(backend).isSameAs(TestOneLoggingBackendBuilder.backend);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("test0");
        });
    }

    /**
     * Creates a new logging backend by using {@link LoggingBackendFactory}.
     *
     * @param properties The configuration properties for the logging backend creation
     * @return The created logging backend
     */
    private LoggingBackend createLoggingBackend(Map<String, String> properties) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Configuration configuration = new Configuration(properties, logger);
        return new LoggingBackendFactory(classLoader, runtime, logger).create(configuration);
    }

    /**
     * Additional logging backend builder with a mocked backend for JUnit tests.
     */
    public static final class TestOneLoggingBackendBuilder implements LoggingBackendBuilder {

        private static final LoggingBackend backend = mock(LoggingBackend.class, "TestLoggingBackend2");

        @Override
        public String getName() {
            return "test1";
        }

        @Override
        public LoggingBackend create(TinylogContext context) {
            return backend;
        }

    }

    /**
     * Additional logging backend builder with a mocked backend for JUnit tests.
     */
    public static final class TestTwoLoggingBackendBuilder implements LoggingBackendBuilder {

        private static final LoggingBackend backend = mock(LoggingBackend.class, "TestLoggingBackend1");

        @Override
        public String getName() {
            return "test2";
        }

        @Override
        public LoggingBackend create(TinylogContext context) {
            return backend;
        }

    }

}
