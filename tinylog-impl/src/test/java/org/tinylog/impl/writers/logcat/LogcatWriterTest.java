package org.tinylog.impl.writers.logcat;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedStatic;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.ClassNamePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.Logcat;

import android.util.Log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@CaptureLogEntries
class LogcatWriterTest {

    @Inject
    private org.tinylog.core.test.log.Log log;

    /**
     * Test for required log entry values.
     */
    @Nested
    class LogEntryValues {

        /**
         * Verifies that the logcat writer will require only {@link LogEntryValue#LEVEL} and log entry values from the
         * passed message placeholder, if no tag placeholder is set.
         */
        @Test
        void untaggedRequiredLogEntryValues() {
            try (LogcatWriter writer = new LogcatWriter(null, new MessagePlaceholder())) {
                assertThat(writer.getRequiredLogEntryValues()).containsExactlyInAnyOrder(
                    LogEntryValue.LEVEL,
                    LogEntryValue.MESSAGE,
                    LogEntryValue.EXCEPTION
                );
            }
        }

        /**
         * Verifies that the logcat writer will require {@link LogEntryValue#LEVEL} and all log entry values from the
         * passed message placeholder as well as from the passed tag placeholder, if both are set.
         */
        @Test
        void taggedRequiredLogEntryValues() {
            try (LogcatWriter writer = new LogcatWriter(new ClassNamePlaceholder(), new MessagePlaceholder())) {
                assertThat(writer.getRequiredLogEntryValues()).containsExactlyInAnyOrder(
                    LogEntryValue.LEVEL,
                    LogEntryValue.CLASS,
                    LogEntryValue.MESSAGE,
                    LogEntryValue.EXCEPTION
                );
            }
        }

    }

    /**
     * Tests for passing log entries to {@link Log} on non-Android platforms.
     */
    @Nested
    @DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    class MockedLogging {

        private MockedStatic<Log> logMock;

        /**
         * Mocks the static {@link Log} class.
         */
        @BeforeEach
        void init() {
            logMock = mockStatic(Log.class);
        }

        /**
         * Restores the statically mocked {@link Log} class.
         */
        @AfterEach
        void dispose() {
            logMock.close();
        }

        /**
         * Verifies that log entries with all {@link Level tinylog severity levels} will be passed to {@link Log}
         * correctly, if no tag placeholder is set.
         *
         * @param tinylogLevel The severity level to test
         * @param androidPriority The corresponding Android priority for the passed severity level
         */
        @ParameterizedTest
        @ArgumentsSource(LevelsAndPrioritiesProvider.class)
        void untaggedLogging(Level tinylogLevel, int androidPriority) {
            LogEntry logEntry = new LogEntryBuilder()
                .severityLevel(tinylogLevel)
                .message("Hello World!")
                .create();

            try (LogcatWriter writer = new LogcatWriter(null, new MessagePlaceholder())) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(androidPriority, null, "Hello World!"));
            }
        }

        /**
         * Verifies that log entries with all {@link Level tinylog severity levels} will be passed to {@link Log}
         * correctly, if a custom tag placeholder is set.
         *
         * @param tinylogLevel The severity level to test
         * @param androidPriority The corresponding Android priority for the passed severity level
         */
        @ParameterizedTest
        @ArgumentsSource(LevelsAndPrioritiesProvider.class)
        void taggedLogging(Level tinylogLevel, int androidPriority) {
            LogEntry logEntry = new LogEntryBuilder()
                .severityLevel(tinylogLevel)
                .className("org.foo.MyClass")
                .message("Hello World!")
                .create();

            try (LogcatWriter writer = new LogcatWriter(new ClassNamePlaceholder(), new MessagePlaceholder())) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(androidPriority, "MyClass", "Hello World!"));
            }
        }

        /**
         * Verifies that a log entry with an illegal severity level is not passed to {@link Log}.
         */
        @Test
        void loggingWithIllegalLevel() {
            LogEntry logEntry = new LogEntryBuilder()
                .severityLevel(Level.OFF)
                .message("Hello Hell!")
                .create();

            try (LogcatWriter writer = new LogcatWriter(null, new MessagePlaceholder())) {
                writer.log(logEntry);
                logMock.verifyNoInteractions();
            }

            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getMessage()).contains(Level.OFF.toString());
            });
        }

    }

    /**
     * Tests for passing log entries to {@link Log} on Android.
     */
    @Nested
    @EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    class RealLogging {

        private Logcat logcat;

        /**
         * Creates an instance of {@link Logcat}.
         */
        @BeforeEach
        void init() {
            logcat = new Logcat();
        }

        /**
         * Verifies that log entries with all {@link Level tinylog severity levels} will be passed to {@link Log}
         * correctly, if no tag placeholder is set.
         *
         * @param tinylogLevel The severity level to test
         * @param androidPriority The corresponding Android priority as letter for the passed severity level
         */
        @ParameterizedTest
        @ArgumentsSource(LevelsAndCharactersProvider.class)
        void untaggedLogging(Level tinylogLevel, char androidPriority) throws IOException {
            LogEntry logEntry = new LogEntryBuilder()
                .severityLevel(tinylogLevel)
                .message("Hello World!")
                .create();

            try (LogcatWriter writer = new LogcatWriter(null, new MessagePlaceholder())) {
                writer.log(logEntry);
            }

            Pattern pattern = Pattern.compile(
                "\\W+" + androidPriority + "\\W+(pl\\.android\\.test?\\W+)?Hello World!$"
            );
            assertThat(logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
        }

        /**
         * Verifies that log entries with all {@link Level tinylog severity levels} will be passed to {@link Log}
         * correctly, if a custom tag placeholder is set.
         *
         * @param tinylogLevel The severity level to test
         * @param androidPriority The corresponding Android priority as letter for the passed severity level
         */
        @ParameterizedTest
        @ArgumentsSource(LevelsAndCharactersProvider.class)
        void taggedLogging(Level tinylogLevel, char androidPriority) throws IOException {
            LogEntry logEntry = new LogEntryBuilder()
                .severityLevel(tinylogLevel)
                .className("org.foo.MyClass")
                .message("Hello World!")
                .create();

            try (LogcatWriter writer = new LogcatWriter(new ClassNamePlaceholder(), new MessagePlaceholder())) {
                writer.log(logEntry);
            }

            Pattern pattern = Pattern.compile("\\W+" + androidPriority + "\\W+MyClass\\W+Hello World!$");
            assertThat(logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
        }

        /**
         * Verifies that a log entry with an illegal severity level is not passed to {@link Log}.
         */
        @Test
        void loggingWithIllegalLevel() throws IOException {
            LogEntry logEntry = new LogEntryBuilder()
                .severityLevel(Level.OFF)
                .message("Hello Hell!")
                .create();

            try (LogcatWriter writer = new LogcatWriter(null, new MessagePlaceholder())) {
                writer.log(logEntry);
            }

            assertThat(logcat.fetchOutput()).noneSatisfy(line -> assertThat(line).contains("Hello Hell!"));

            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getMessage()).contains(Level.OFF.toString());
            });
        }

        /**
         * Verifies that flushing won't throw any exception.
         */
        @Test
        void flushing() {
            try (LogcatWriter writer = new LogcatWriter(null, new MessagePlaceholder())) {
                writer.flush();
            }
        }

    }

    /**
     * Arguments provider for providing all {@link Level tinylog severity levels} with the corresponding Android
     * priority numbers.
     *
     * @see MockedLogging#untaggedLogging(Level, int)
     * @see MockedLogging#taggedLogging(Level, int)
     */
    private static class LevelsAndPrioritiesProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            /* START IGNORE CODE STYLE */
            return Stream.of(
                Arguments.of(Level.TRACE, Log.VERBOSE),
                Arguments.of(Level.DEBUG, Log.DEBUG  ),
                Arguments.of(Level.INFO,  Log.INFO   ),
                Arguments.of(Level.WARN,  Log.WARN   ),
                Arguments.of(Level.ERROR, Log.ERROR  )
            );
            /* END IGNORE CODE STYLE */
        }

    }

    /**
     * Arguments provider for providing all {@link Level tinylog severity levels} with the corresponding Android
     * priority characters.
     *
     * @see RealLogging#untaggedLogging(Level, char)
     * @see RealLogging#taggedLogging(Level, char)
     */
    private static class LevelsAndCharactersProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            /* START IGNORE CODE STYLE */
            return Stream.of(
                Arguments.of(Level.TRACE, 'V'),
                Arguments.of(Level.DEBUG, 'D'),
                Arguments.of(Level.INFO,  'I'),
                Arguments.of(Level.WARN,  'W'),
                Arguments.of(Level.ERROR, 'E')
            );
            /* END IGNORE CODE STYLE */
        }

    }

}
