package org.tinylog.core.backend;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Level;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.MessageFormatter;

import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BundleLoggingBackendTest {

	/**
	 * Verifies that the provided context storage is based on the context storage of the child logging backends.
	 */
	@Test
	void contextStorage() {
		ContextStorage firstStorage = mock(ContextStorage.class);
		LoggingBackend firstBackend = mock(LoggingBackend.class);
		when(firstBackend.getContextStorage()).thenReturn(firstStorage);
		when(firstStorage.getMapping()).thenReturn(Collections.singletonMap("foo", "1"));

		ContextStorage secondStorage = mock(ContextStorage.class);
		LoggingBackend secondBackend = mock(LoggingBackend.class);
		when(secondBackend.getContextStorage()).thenReturn(secondStorage);
		when(secondStorage.getMapping()).thenReturn(Collections.singletonMap("bar", "2"));

		BundleLoggingBackend bundleBackend = new BundleLoggingBackend(Arrays.asList(firstBackend, secondBackend));
		assertThat(bundleBackend.getContextStorage().getMapping())
			.containsExactlyInAnyOrderEntriesOf(ImmutableMap.of("foo", "1", "bar", "2"));
	}

	/**
	 * Verifies that all passed child logging backends are stored.
	 */
	@Test
	void childProviders() {
		LoggingBackend first = mock(LoggingBackend.class);
		LoggingBackend second = mock(LoggingBackend.class);
		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.getChildren()).containsExactlyInAnyOrder(first, second);
	}

	/**
	 * Verifies that the level visibilities of all child logging backends are correctly merged for fully-qualified
	 * class names.
	 *
	 * @param className The fully-qualified class name to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Foo", "example.Foo", "org.tinylog.core.backend.BundleLoggingBackend"})
	void classesVisibility(String className) {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.getLevelVisibilityByClass(className)).thenReturn(
			new LevelVisibility(
				OutputDetails.DISABLED,
				OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
				OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION
			)
		);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.getLevelVisibilityByClass(className)).thenReturn(
			new LevelVisibility(
				OutputDetails.DISABLED,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
			)
		);

		BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));
		LevelVisibility visibility = backend.getLevelVisibilityByClass(className);
		assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
		assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
		assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
		assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
		assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION);
	}

	/**
	 * Verifies that the level visibilities of all child logging backends are correctly merged for tags.
	 *
	 * @param tag The category tag to test
	 */
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"tinylog", "foo"})
	void tagsVisibility(String tag) {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.getLevelVisibilityByTag(tag)).thenReturn(
			new LevelVisibility(
				OutputDetails.DISABLED,
				OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
				OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION
			)
		);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.getLevelVisibilityByTag(tag)).thenReturn(
			new LevelVisibility(
				OutputDetails.DISABLED,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
				OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
			)
		);

		BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));
		LevelVisibility visibility = backend.getLevelVisibilityByTag(tag);
		assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
		assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
		assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
		assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
		assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION);
	}

	/**
	 * Verifies that {@link NopLoggingBackend#isEnabled(Object, String, Level)} returns {@code false}, if logging
	 * is disabled for all child logging backends.
	 */
	@Test
	void allDisabled() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.isEnabled(any(), any(), any())).thenReturn(false);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.isEnabled(any(), any(), any())).thenReturn(false);

		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.isEnabled("org.tinylog.Foo", "TEST", Level.INFO)).isFalse();

		verify(first).isEnabled(eq("org.tinylog.Foo"), eq("TEST"), eq(Level.INFO));
		verify(second).isEnabled(eq("org.tinylog.Foo"), eq("TEST"), eq(Level.INFO));
	}

	/**
	 * Verifies that {@link NopLoggingBackend#isEnabled(Object, String, Level)} returns {@code true}, if logging
	 * is enabled for at least one child logging backend.
	 */
	@Test
	void partlyEnabled() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.isEnabled(any(), any(), any())).thenReturn(false);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.isEnabled(any(), any(), any())).thenReturn(true);

		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.isEnabled("org.tinylog.Foo", "TEST", Level.INFO)).isTrue();

		verify(first).isEnabled(eq("org.tinylog.Foo"), eq("TEST"), eq(Level.INFO));
		verify(second).isEnabled(eq("org.tinylog.Foo"), eq("TEST"), eq(Level.INFO));
	}

	/**
	 * Verifies that {@link NopLoggingBackend#isEnabled(Object, String, Level)} returns {@code true}, if logging
	 * is enabled for all child logging backends.
	 */
	@Test
	void allEnabled() {
		LoggingBackend first = mock(LoggingBackend.class);
		when(first.isEnabled(any(), any(), any())).thenReturn(true);

		LoggingBackend second = mock(LoggingBackend.class);
		when(second.isEnabled(any(), any(), any())).thenReturn(true);

		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.isEnabled("org.tinylog.Foo", "TEST", Level.INFO)).isTrue();

		verify(first).isEnabled(eq("org.tinylog.Foo"), eq("TEST"), eq(Level.INFO));
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

		Throwable throwable = new Throwable();
		Object[] arguments = {"world"};
		MessageFormatter formatter = mock(MessageFormatter.class);
		backend.log("org.tinylog.Foo", "TEST", Level.INFO, throwable, "Hello {}!", arguments, formatter);

		verify(first).log(
			eq("org.tinylog.Foo"),
			eq("TEST"),
			eq(Level.INFO),
			same(throwable),
			eq("Hello {}!"),
			same(arguments),
			same(formatter)
		);

		verify(second).log(
			eq("org.tinylog.Foo"),
			eq("TEST"),
			eq(Level.INFO),
			same(throwable),
			eq("Hello {}!"),
			same(arguments),
			same(formatter)
		);
	}

}
