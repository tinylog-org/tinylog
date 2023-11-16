package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Level;
import org.tinylog.core.context.ContextStorage;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
     * Verifies that all severity levels are disabled in the precalculated level visibility object for all classes.
     *
     * @param className The fully-qualified class name
     */
    @ParameterizedTest
    @ValueSource(strings = {"Foo", "example.Foo", "org.tinylog.core.backend.NopLoggingBackend"})
    void classVisibility(String className) {
        LevelVisibility visibility = new NopLoggingBackend().getLevelVisibilityByClass(className);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that all severity levels are disabled in the precalculated level visibility object for all tags.
     *
     * @param tag The category tag to test
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"tinylog", "foo"})
    void tagVisibility(String tag) {
        LevelVisibility visibility = new NopLoggingBackend().getLevelVisibilityByTag(tag);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that logging is disabled for all severity levels.
     *
     * @param level The severity level to test
     */
    @ParameterizedTest
    @EnumSource(Level.class)
    void allLevelsDisabled(Level level) {
        NopLoggingBackend backend = new NopLoggingBackend();
        assertThat(backend.isEnabled(null, null, level)).isFalse();
    }

    /**
     * Verifies that log entries are accepted.
     */
    @Test
    void acceptLogEntries() {
        new NopLoggingBackend().log(
            null, null, Level.TRACE, null, "Hello world!", null, null
        );
    }

    /**
     * Verifies that reconfiguration leads to the same logging backend.
     */
    @Test
    void reconfigure() {
        LoggingBackend backend = new NopLoggingBackend();
        assertThat(backend.reconfigure()).isSameAs(backend);
    }

}
