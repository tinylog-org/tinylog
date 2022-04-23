package org.slf4j.impl;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.spi.MDCAdapter;
import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.slf4j.TinylogMdcAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

// START IGNORE RULES: AbbreviationAsWordInName
class StaticMDCBinderTest {

	/**
	 * Verifies that the static binder returns {@link TinylogMdcAdapter} as MDC adapter.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	void provideMdcAdaptorInstance() {
		ContextStorage storage = mock(ContextStorage.class);
		LoggingBackend backend = mock(LoggingBackend.class);
		Framework framework = mock(Framework.class);

		when(backend.getContextStorage()).thenReturn(storage);
		when(framework.getLoggingBackend()).thenReturn(backend);

		try (MockedStatic<Tinylog> tinylogMock = mockStatic(Tinylog.class)) {
			tinylogMock.when(Tinylog::getFramework).thenReturn(framework);
			MDCAdapter adapter = StaticMDCBinder.SINGLETON.getMDCA();
			assertThat(adapter).isInstanceOf(TinylogMdcAdapter.class);
		}
	}

	/**
	 * Verifies that the static binder returns the fully-qualified class name of {@link TinylogMdcAdapter} as MDC
	 * adapter class name.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	void provideMdcAdaptorClassName() {
		ContextStorage storage = mock(ContextStorage.class);
		LoggingBackend backend = mock(LoggingBackend.class);
		Framework framework = mock(Framework.class);

		when(backend.getContextStorage()).thenReturn(storage);
		when(framework.getLoggingBackend()).thenReturn(backend);

		try (MockedStatic<Tinylog> tinylogMock = mockStatic(Tinylog.class)) {
			tinylogMock.when(Tinylog::getFramework).thenReturn(framework);
			String className = StaticMDCBinder.SINGLETON.getMDCAdapterClassStr();
			assertThat(className).isEqualTo(TinylogMdcAdapter.class.getName());
		}
	}

}
