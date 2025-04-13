package org.tinylog.impl.writer.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholder;
import org.tinylog.impl.format.placeholder.MethodPlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.format.placeholder.TagPlaceholder;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class LogcatWriterTest {

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

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
     * Verifies that untagged log entries are correctly output.
     *
     * @param levelCode The expected Logcat level code
     * @param severityLevel The tinylog severity level
     * @throws IOException If failed to read the output from Logcat
     */
    @ParameterizedTest
    @CsvSource({
        "V, TRACE",
        "D, DEBUG",
        "I, INFO",
        "W, WARN",
        "E, ERROR"
    })
    void untaggedOutput(String levelCode, Level severityLevel) throws IOException {
        Placeholder placeholder = new MessageOnlyPlaceholder(configuration);
        try (LogcatWriter writer = new LogcatWriter(null, placeholder, logger)) {
            writer.log(new LogEntryBuilder().severityLevel(severityLevel).tag("foo").message("Default tag!").create());
            assertThat(fetchOutput()).anyMatch(line -> line.matches(levelCode + "/(tinylog.test)? *: Default tag!"));
        }
    }

    /**
     * Verifies that custom tagged log entries are correctly output.
     *
     * @param levelCode The expected Logcat level code
     * @param severityLevel The tinylog severity level
     * @throws IOException If failed to read the output from Logcat
     */
    @ParameterizedTest
    @CsvSource({
        "V, TRACE",
        "D, DEBUG",
        "I, INFO",
        "W, WARN",
        "E, ERROR"
    })
    void taggedOutput(String levelCode, Level severityLevel) throws IOException {
        Placeholder tagPlaceholder = new TagPlaceholder();
        Placeholder messagePlaceholder = new MessageOnlyPlaceholder(configuration);
        try (LogcatWriter writer = new LogcatWriter(tagPlaceholder, messagePlaceholder, logger)) {
            writer.log(new LogEntryBuilder().severityLevel(severityLevel).tag("foo").message("My foo tag!").create());
            assertThat(fetchOutput()).anyMatch(line -> line.matches(levelCode + "/foo *: My foo tag!"));
        }
    }

    /**
     * Verifies that a log entry with an illegal severity level is not output, but reported as error.
     */
    @Test
    void loggingWithIllegalLevel() throws IOException {
        Placeholder placeholder = new MessageOnlyPlaceholder(configuration);
        try (LogcatWriter writer = new LogcatWriter(null, placeholder, logger)) {
            writer.log(new LogEntryBuilder().severityLevel(Level.OFF).message("Hello World!").create());
            assertThat(fetchOutput()).noneMatch(line -> line.contains("Hello World!"));
        }

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains(Level.OFF.toString());
        });
    }

    /**
     * Fetches the output from Logcat.
     *
     * @return Each list element represents one log entry line from Logcat
     * @throws IOException If failed to read the output from Logcat
     */
    private static List<String> fetchOutput() throws IOException {
        Process process = Runtime.getRuntime().exec("logcat -d -v tag");
        try (InputStream stream = process.getInputStream()) {
            try (Reader inputReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputReader)) {
                    return bufferedReader.lines().collect(Collectors.toList());
                }
            }
        } finally {
            process.destroy();
        }
    }

}
