package org.tinylog.impl.format;

import java.time.Instant;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.PlaceholderBuilder;
import org.tinylog.impl.format.style.StyleBuilder;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.junit.service.RegisterService;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog(configuration = {"locale=en_US", "zone=UTC"})
class FormatPatternParserTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Verifies that plain static text is output unchanged.
     */
    @Test
    void plaintText() {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format("Hello World!", logEntry)).isEqualTo("Hello World!");
    }

    /**
     * Verifies that new lines in all known formats are converted into the line format of the current operating system.
     *
     * @param pattern The format pattern with new lines to test
     * @param expected The expected render output including a new line in the format of the current operating
     */
    @ParameterizedTest
    @ArgumentsSource(NewLineExamplesProvider.class)
    void normalizeNewLines(String pattern, String expected) {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format(pattern, logEntry)).isEqualTo(expected);
    }

    /**
     * Verifies that grouped static text is output unchanged without any curly brackets.
     */
    @Test
    void groupedText() {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format("{1} + {2} = {3}", logEntry)).isEqualTo("1 + 2 = 3");
    }

    /**
     * Verifies that escaped curly brackets are output.
     */
    @Test
    void escapedGroups() {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format("'{1}' + '{2}' = '{3}'", logEntry)).isEqualTo("{1} + {2} = {3}");
    }

    /**
     * Verifies that escaped static text is output without quotes.
     */
    @Test
    void escapedTextInGroups() {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format("{'1'} + {'2'} = {'3'}", logEntry)).isEqualTo("1 + 2 = 3");
    }

    /**
     * Verifies that escaped pipes are output without quotes.
     */
    @Test
    void escapedPipes() {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format("1 '|' 2 '|' 3", logEntry)).isEqualTo("1 | 2 | 3");
    }

    /**
     * Verifies that a placeholder can be resolved, if the entire format pattern is a placeholder.
     */
    @Test
    void resolveWholePlaceholder() {
        LogEntry logEntry = new LogEntryBuilder().className("foo.Bar").create();
        assertThat(format("class", logEntry)).isEqualTo("foo.Bar");
    }

    /**
     * Verifies that a placeholder in curly brackets can be resolved.
     */
    @Test
    void resolveSubPlaceholder() {
        LogEntry logEntry = new LogEntryBuilder().className("foo.Bar").create();
        assertThat(format("Class <{class}>", logEntry)).isEqualTo("Class <foo.Bar>");
    }

    /**
     * Verifies that a placeholder nested in multiple curly brackets can be resolved.
     */
    @Test
    void resolveNestedPlaceholder() {
        LogEntry logEntry = new LogEntryBuilder().className("foo.Bar").create();
        assertThat(format("Class {<{class}>}", logEntry)).isEqualTo("Class <foo.Bar>");
    }

    /**
     * Verifies that a placeholder with a plain unescaped parameter can be resolved.
     */
    @Test
    void resolvePlainParameterizedPlaceholder() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(format("date: HH:mm", logEntry)).isEqualTo("00:00");
    }

    /**
     * Verifies that a placeholder with a partly escaped parameter can be resolved.
     */
    @Test
    void resolveEscapedParameterizedPlaceholder() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(format("date: H'h'mm", logEntry)).isEqualTo("0h00");
    }

    /**
     * Verifies that a non-instantiable placeholder is reported.
     */
    @RegisterService(service = PlaceholderBuilder.class, implementations = EvilBuilder.class)
    @Test
    void resolveNonInstantiablePlaceholder() {
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(format("evil", logEntry)).isEqualTo("evil");
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("evil");
            assertThat(entry.getThrowable()).isInstanceOf(UnsupportedOperationException.class);
        });
    }

    /**
     * Verifies that a single style can be applied to a placeholder.
     */
    @Test
    void applySingleStyle() {
        LogEntry logEntry = new LogEntryBuilder().message("Hello!").create();
        assertThat(format("message | max-length: 5", logEntry)).isEqualTo("He...");
    }

    /**
     * Verifies that multiple styles can be applied to a placeholder.
     */
    @Test
    void applyMultipleStyles() {
        LogEntry logEntry = new LogEntryBuilder().message("Hello!").create();
        assertThat(format("message | min-length: 5 | max-length: 5", logEntry)).isEqualTo("He...");

        logEntry = new LogEntryBuilder().message("Hi!").create();
        assertThat(format("message | min-length: 5 | max-length: 5", logEntry)).isEqualTo("Hi!  ");
    }

    /**
     * Verifies that an unknown style is reported and does not influence the output of the placeholder.
     */
    @Test
    void reportNonExistentStyle() {
        LogEntry logEntry = new LogEntryBuilder().message("Hello!").create();
        assertThat(format("message | foo", logEntry)).isEqualTo("Hello!");
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

    /**
     * Verifies that a non-instantiable style is reported and does not influence the output of the placeholder.
     */
    @RegisterService(service = StyleBuilder.class, implementations = EvilBuilder.class)
    @Test
    void reportNonInstantiableStyle() {
        LogEntry logEntry = new LogEntryBuilder().message("Hello!").create();
        assertThat(format("message | evil", logEntry)).isEqualTo("Hello!");
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("evil");
            assertThat(entry.getThrowable()).isInstanceOf(UnsupportedOperationException.class);
        });
    }

    /**
     * Parses and renders a format pattern by using {@link FormatPatternParser}.
     *
     * @param pattern The format pattern to parse
     * @param logEntry The log entry to render
     * @return The formatted log entry
     */
    private String format(String pattern, LogEntry logEntry) {
        Placeholder placeholder = new FormatPatternParser(context).parse(pattern);
        return new FormatOutputRenderer(placeholder).render(logEntry);
    }

    /**
     * Arguments provider for providing tuples of the source format pattern and the expected rendered output.
     *
     * @see #normalizeNewLines(String, String)
     */
    private static final class NewLineExamplesProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of("Unix\n", "Unix" + System.lineSeparator()),
                Arguments.of("Classic Mac\n", "Classic Mac" + System.lineSeparator()),
                Arguments.of("Windows\r\n", "Windows" + System.lineSeparator())
            );
        }

    }

    /**
     * Placeholder builder that always throws an {@link UnsupportedOperationException} when trying to create a new
     * placeholder or style.
     */
    public static class EvilBuilder implements PlaceholderBuilder, StyleBuilder {

        @Override
        public String getName() {
            return "evil";
        }

        @Override
        public Placeholder create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Placeholder create(TinylogContext context, Placeholder placeholder, String value) {
            throw new UnsupportedOperationException();
        }

    }

}
