package org.tinylog.slf4j;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.SLF4JServiceProvider;
import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.context.ContextStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class TinylogServiceProviderTest {

    /**
     * Verifies that both factories and the MDC adopter are provided.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void factories() {
        ContextStorage storage = mock(ContextStorage.class);
        LoggingBackend backend = mock(LoggingBackend.class);
        Framework framework = mock(Framework.class);

        when(backend.getContextStorage()).thenReturn(storage);
        when(framework.getLoggingBackend()).thenReturn(backend);

        try (MockedStatic<Tinylog> tinylogMock = mockStatic(Tinylog.class)) {
            tinylogMock.when(Tinylog::getFramework).thenReturn(framework);

            TinylogServiceProvider provider = new TinylogServiceProvider();
            provider.initialize();

            assertThat(provider.getLoggerFactory()).isInstanceOf(TinylogLoggerFactory.class);
            assertThat(provider.getMarkerFactory()).isInstanceOf(BasicMarkerFactory.class);
            assertThat(provider.getMDCAdapter()).isInstanceOf(TinylogMdcAdapter.class);
        }
    }

    /**
     * Verifies that the requested API version is SLF4J 2.0.
     */
    @Test
    void apiVersion() {
        TinylogServiceProvider provider = new TinylogServiceProvider();
        assertThat(provider.getRequestedApiVersion()).isEqualTo("2.0");
    }

    /**
     * Verifies that the provider is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(SLF4JServiceProvider.class))
            .singleElement()
            .isInstanceOf(TinylogServiceProvider.class);
    }

}
