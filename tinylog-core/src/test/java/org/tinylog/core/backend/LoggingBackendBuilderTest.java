package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = LoggingBackendBuilder.class,
    implementations = {
        LoggingBackendBuilderTest.FirstLoggingBackendBuilder.class,
        LoggingBackendBuilderTest.SecondLoggingBackendBuilder.class
    }
)
class LoggingBackendBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all logging backend builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(LoggingBackendBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstLoggingBackendBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondLoggingBackendBuilder.class));
    }

    /**
     * First logging backend builder service implementation.
     */
    public static final class FirstLoggingBackendBuilder implements LoggingBackendBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public LoggingBackend create(TinylogContext context) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second logging backend builder service implementation.
     */
    public static final class SecondLoggingBackendBuilder implements LoggingBackendBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public LoggingBackend create(TinylogContext context) {
            throw new UnsupportedOperationException();
        }

    }

}
