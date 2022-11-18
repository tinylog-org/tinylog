package org.slf4j.impl;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.ILoggerFactory;
import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;
import org.tinylog.slf4j.TinylogLoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class StaticLoggerBinderTest {

    /**
     * Verifies that SLF4J version 1.7 is requested.
     */
    @Test
    void requestSupportedVersion() {
        assertThat(StaticLoggerBinder.REQUESTED_API_VERSION).isEqualTo("1.7");
    }

    /**
     * Verifies that the static binder returns {@link TinylogLoggerFactory} as logger factory.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void provideLoggerFactoryInstance() {
        Framework framework = mock(Framework.class);

        try (MockedStatic<Tinylog> tinylogMock = mockStatic(Tinylog.class)) {
            tinylogMock.when(Tinylog::getFramework).thenReturn(framework);
            ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
            assertThat(factory).isInstanceOf(TinylogLoggerFactory.class);
        }
    }

    /**
     * Verifies that the static binder returns the fully-qualified class name of {@link TinylogLoggerFactory} as logger
     * factory class name.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void provideLoggerFactoryClassName() {
        Framework framework = mock(Framework.class);

        try (MockedStatic<Tinylog> tinylogMock = mockStatic(Tinylog.class)) {
            tinylogMock.when(Tinylog::getFramework).thenReturn(framework);
            String className = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
            assertThat(className).isEqualTo(TinylogLoggerFactory.class.getName());
        }
    }

}
