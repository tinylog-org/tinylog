package org.tinylog.core.backend;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.MessageFormatter;

import static java.util.Collections.emptyMap;
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
        when(firstStorage.getMapping()).thenReturn(Map.of("foo", "1"));

        ContextStorage secondStorage = mock(ContextStorage.class);
        LoggingBackend secondBackend = mock(LoggingBackend.class);
        when(secondBackend.getContextStorage()).thenReturn(secondStorage);
        when(secondStorage.getMapping()).thenReturn(Map.of("bar", "2"));

        BundleLoggingBackend bundleBackend = new BundleLoggingBackend(Arrays.asList(firstBackend, secondBackend));
        assertThat(bundleBackend.getContextStorage().getMapping())
            .containsExactlyInAnyOrderEntriesOf(Map.of("foo", "1", "bar", "2"));
    }

    /**
     * Verifies that the level visibilities of all child logging backends are correctly merged for fully-qualified
     * class names.
     *
     * @param className The fully-qualified class name to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"Foo", "example.Foo", "org.tinylog.core.backend.BundleLoggingBackendTest"})
    void classesVisibility(String className) {
        LoggingBackend first = mock(LoggingBackend.class);
        when(first.getLevelVisibilityByClass(className)).thenReturn(
            new LevelVisibility(
                OutputDetails.DISABLED,
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFO,
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFO,
                OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO
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
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
    }

    /**
     * Verifies that the level visibilities of all child logging backends are correctly merged for category tags.
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
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFO,
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFO,
                OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO
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
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
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
     *
     * @param last {@code true} if this is the last log entry to be currently processed, {@code false} if there
     *             are still other log entries to be processed
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void provideLogsToChildren(boolean last) {
        LoggingBackend first = mock(LoggingBackend.class);
        LoggingBackend second = mock(LoggingBackend.class);
        BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));

        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            "Foo",
            "bar",
            Level.INFO,
            new Throwable(),
            mock(MessageFormatter.class),
            "Hello {}!",
            new Object[] {"Alice"}
        );

        backend.output(entry, last);

        verify(first).output(same(entry), eq(last));
        verify(second).output(same(entry), eq(last));
    }

    /**
     * Verifies that all child backends are closed correctly.
     */
    @Test
    void closeChildren() {
        LoggingBackend first = mock(LoggingBackend.class);
        LoggingBackend second = mock(LoggingBackend.class);
        BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));

        verify(first, never()).close();
        verify(second, never()).close();

        backend.close();

        verify(first).close();
        verify(second).close();
    }

}
