package org.tinylog.impl.writer.logcat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedStatic;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.MessagePlaceholder;
import org.tinylog.impl.format.placeholder.MethodPlaceholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.format.placeholder.TagPlaceholder;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import android.util.Log;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@Tinylog
class LogcatWriterTest {

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private org.tinylog.test.junit.log.Log log;

    /**
     * Verifies that the Logcat writer returns the output details of the passed tag placeholder.
     */
    @Test
    void provideOutputDetailsFromTagPlaceholder() {
        try (LogcatWriter writer = new LogcatWriter(new ClassPlaceholder(), new StaticTextPlaceholder("foo"), logger)) {
            assertThat(writer.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        }
    }

    /**
     * Verifies that the Logcat writer returns the output details of the passed message placeholder.
     */
    @Test
    void provideOutputDetailsFromMessagePlaceholder() {
        try (LogcatWriter writer = new LogcatWriter(new TagPlaceholder(), new MethodPlaceholder(), logger)) {
            assertThat(writer.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }
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
    void untaggedOutput(Level tinylogLevel, int androidPriority) {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(tinylogLevel)
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (LogcatWriter writer = new LogcatWriter(
                null,
                new MessagePlaceholder(configuration),
                logger
            )) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(androidPriority, null, "Hello World!"));
            }
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
    void taggedOutput(Level tinylogLevel, int androidPriority) {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(tinylogLevel)
            .tag("foo")
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (LogcatWriter writer = new LogcatWriter(
                new TagPlaceholder(),
                new MessagePlaceholder(configuration),
                logger
            )) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(androidPriority, "foo", "Hello World!"));
            }
        }
    }

    /**
     * Verifies that a log entry with an illegal severity level is not passed to {@link Log}.
     */
    @Test
    void loggingWithIllegalLevel() {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.OFF)
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (LogcatWriter writer = new LogcatWriter(
                null,
                new MessagePlaceholder(configuration),
                logger
            )) {
                writer.log(logEntry);
                logMock.verifyNoInteractions();
            }
        }

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains(Level.OFF.toString());
        });
    }

    /**
     * Arguments provider for providing all {@link Level tinylog severity levels} with the corresponding Android
     * priority numbers.
     *
     * @see #untaggedOutput(Level, int)
     * @see #taggedOutput(Level, int)
     */
    private static final class LevelsAndPrioritiesProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(Level.TRACE, Log.VERBOSE),
                Arguments.of(Level.DEBUG, Log.DEBUG),
                Arguments.of(Level.INFO, Log.INFO),
                Arguments.of(Level.WARN, Log.WARN),
                Arguments.of(Level.ERROR, Log.ERROR)
            );
        }

    }

}
