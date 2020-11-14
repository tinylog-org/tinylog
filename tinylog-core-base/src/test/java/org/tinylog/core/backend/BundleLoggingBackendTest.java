package org.tinylog.core.backend;

import java.util.Arrays;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BundleLoggingBackendTest {

	/**
	 * Verifies that all passed child logging backends are stored.
	 */
	@Test
	void childProviders() {
		LoggingBackend first = mock(LoggingBackend.class);
		LoggingBackend second = mock(LoggingBackend.class);
		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.getProviders()).containsExactlyInAnyOrder(first, second);
	}

	/**
	 * Verifies that the level visibility of all child logging backends is included.
	 */
	@Test
	void visibility() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.getLevelVisibility("foo")).thenReturn(
			new LevelVisibility(false, false, false, true, true)
		);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.getLevelVisibility("foo")).thenReturn(
			new LevelVisibility(false, false, true, true, true)
		);

		BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));
		LevelVisibility visibility = backend.getLevelVisibility("foo");

		AssertionsForClassTypes.assertThat(visibility.isTraceEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isDebugEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isInfoEnabled()).isTrue();
		AssertionsForClassTypes.assertThat(visibility.isWarnEnabled()).isTrue();
		AssertionsForClassTypes.assertThat(visibility.isErrorEnabled()).isTrue();
	}

	/**
	 * Verifies that {@link NopLoggingBackend#isEnabled(StackTraceLocation, String, Level)} returns {@code false}, if
	 * all logging is disabled for all child logging backends.
	 */
	@Test
	void allDisabled() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.isEnabled(any(), any(), any())).thenReturn(false);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.isEnabled(any(), any(), any())).thenReturn(false);

		StackTraceLocation location = mock(StackTraceLocation.class);

		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.isEnabled(location, "TEST", Level.INFO)).isFalse();

		verify(first).isEnabled(not(same(location)), eq("TEST"), eq(Level.INFO));
		verify(second).isEnabled(not(same(location)), eq("TEST"), eq(Level.INFO));
	}

	/**
	 * Verifies that {@link NopLoggingBackend#isEnabled(StackTraceLocation, String, Level)} returns {@code true}, if
	 * all logging is enabled for at least one child logging backend.
	 */
	@Test
	void partlyEnabled() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.isEnabled(any(), any(), any())).thenReturn(false);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.isEnabled(any(), any(), any())).thenReturn(true);

		StackTraceLocation location = mock(StackTraceLocation.class);

		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.isEnabled(location, "TEST", Level.INFO)).isTrue();

		verify(first).isEnabled(not(same(location)), eq("TEST"), eq(Level.INFO));
		verify(second).isEnabled(not(same(location)), eq("TEST"), eq(Level.INFO));
	}

	/**
	 * Verifies that {@link NopLoggingBackend#isEnabled(StackTraceLocation, String, Level)} returns {@code true}, if
	 * all logging is enabled for all child logging backends.
	 */
	@Test
	void allEnabled() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.isEnabled(any(), any(), any())).thenReturn(true);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.isEnabled(any(), any(), any())).thenReturn(true);

		StackTraceLocation location = mock(StackTraceLocation.class);

		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.isEnabled(location, "TEST", Level.INFO)).isTrue();

		verify(first).isEnabled(not(same(location)), eq("TEST"), eq(Level.INFO));
		verify(second, never()).isEnabled(any(), any(), any());
	}

	/**
	 * Verifies that log entries are passed to all assigned child backends.
	 */
	@Test
	void provideLogsToChildren() {
		LoggingBackend first = mock(LoggingBackend.class);
		LoggingBackend second = mock(LoggingBackend.class);
		BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));

		StackTraceLocation location = mock(StackTraceLocation.class);
		Throwable throwable = new Throwable();
		Object[] arguments = {"world"};
		MessageFormatter formatter = mock(MessageFormatter.class);
		backend.log(location, "TEST", Level.INFO, throwable, "Hello {}!", arguments, formatter);

		verify(first).log(
			not(same(location)),
			eq("TEST"),
			eq(Level.INFO),
			same(throwable),
			eq("Hello {}!"),
			same(arguments),
			same(formatter)
		);

		verify(second).log(
			not(same(location)),
			eq("TEST"),
			eq(Level.INFO),
			same(throwable),
			eq("Hello {}!"),
			same(arguments),
			same(formatter)
		);

		verify(location).push();
	}

}
