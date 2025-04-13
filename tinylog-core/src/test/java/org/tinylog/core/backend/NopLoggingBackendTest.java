package org.tinylog.core.backend;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.ContextStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class NopLoggingBackendTest {

    /**
     * Verifies that the provided context storage does not store any context values.
     */
    @Test
    void contextStorage() {
        ContextStorage storage = new NopLoggingBackend().getContextStorage();
        storage.put("foo", "42");
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that all severity levels are disabled for all classes in the precalculated level visibility object.
     *
     * @param className The fully-qualified class name
     */
    @ParameterizedTest
    @ValueSource(strings = {"Foo", "example.Foo", "org.tinylog.core.backend.NopLoggingBackend"})
    void classVisibility(String className) {
        LevelVisibility visibility = new NopLoggingBackend().getLevelVisibilityByClass(className);
        AssertionsForInterfaceTypes.assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that all severity levels are disabled for all tags in the precalculated level visibility object.
     *
     * @param tag The category tag to test
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"tinylog", "foo"})
    void tagVisibility(String tag) {
        LevelVisibility visibility = new NopLoggingBackend().getLevelVisibilityByTag(tag);
        AssertionsForInterfaceTypes.assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        AssertionsForInterfaceTypes.assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that the output is disabled for all severity levels.
     *
     * @param level The severity level to test
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "OFF")
    void allSeverityLevelsDisabled(Level level) {
        NopLoggingBackend backend = new NopLoggingBackend();
        assertThat(backend.isEnabled(null, null, level)).isFalse();
    }

    /**
     * Verifies that passed log entries are ignored.
     *
     * @param last The flag whether a log entry is the last
     */
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ignoreLogEntries(boolean last) {
        LogEntry entry = mock(LogEntry.class);
        new NopLoggingBackend().output(entry, last);
        verifyNoInteractions(entry);
    }

    /**
     * Verifies that the logging backend can be closed without throwing any exception.
     */
    @Test
    void closable() {
        new NopLoggingBackend().close();
    }

}
