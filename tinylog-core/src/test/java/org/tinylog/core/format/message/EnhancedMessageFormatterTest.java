package org.tinylog.core.format.message;

import java.text.ChoiceFormat;
import java.time.LocalTime;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog(configuration = "locale=en_US")
class EnhancedMessageFormatterTest {

    @Inject
    private ClassLoader loader;

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

    /**
     * Verifies that a string can be formatted without defining a format pattern.
     */
    @Test
    void formatDefaultStringWithoutPattern() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "Hello {}!", "Alice");
        assertThat(output).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that format patterns are silently ignored for strings.
     */
    @Test
    void ignorePatternForStrings() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "Hello {###}!", "Alice");
        assertThat(output).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that a number can be formatted without defining a format pattern.
     */
    @Test
    void formatNumberWithoutPattern() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "The maximum port number is {}.", 65535);
        assertThat(output).isEqualTo("The maximum port number is 65535.");
    }

    /**
     * Verifies that a number can be formatted with a format pattern.
     */
    @Test
    void formatNumberWithPattern() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "Pi is {0.00}", Math.PI);
        assertThat(output).isEqualTo("Pi is 3.14");
    }

    /**
     * Verifies that a local time can be formatted with a format pattern.
     */
    @Test
    void formatTimeWithPattern() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "It is {hh:mm a}.", LocalTime.of(12, 30));
        assertThat(output).isEqualTo("It is 12:30 PM.");
    }

    /**
     * Verifies that a lazy argument can be formatted.
     */
    @Test
    void formatLazyArgument() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        Supplier<?> supplier = () -> "Alice";
        String output = formatter.format(configuration, "Hello {}!", supplier);
        assertThat(output).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that multiple arguments can be formatted.
     */
    @Test
    void formatMultipleArguments() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "{} + {} = {}", 1, 2, 3);
        assertThat(output).isEqualTo("1 + 2 = 3");
    }

    /**
     * Verifies that placeholders without matching arguments are silently ignored.
     */
    @Test
    void ignoreSuperfluousPlaceholders() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "{}, {}, and {}", 1, 2);
        assertThat(output).isEqualTo("1, 2, and {}");
    }

    /**
     * Verifies that superfluous arguments are silently ignored.
     */
    @Test
    void ignoreSuperfluousArguments() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "{}, {}, and {}", 1, 2, 3, 4);
        assertThat(output).isEqualTo("1, 2, and 3");
    }

    /**
     * Verifies that invalid format patterns are reported.
     */
    @Test
    void reportInvalidPatterns() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "<{0 # 0}>", 42);
        assertThat(output).isEqualTo("<42>");

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("0 # 0", "42");
        });
    }

    /**
     * Verifies that curly brackets can be escaped by single quotes.
     */
    @Test
    void escapeCurlyBrackets() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "Brackets can be escaped ('{}') or replaced ({})", 42);
        assertThat(output).isEqualTo("Brackets can be escaped ({}) or replaced (42)");
    }

    /**
     * Verifies that phrases in format patterns can be escaped by single quotes.
     */
    @Test
    void escapePhraseInPatterns() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "It is {hh 'o''clock'}.", LocalTime.of(12, 0));
        assertThat(output).isEqualTo("It is 12 o'clock.");
    }

    /**
     * Verifies that curly brackets can be nested and used as part of a format pattern.
     */
    @Test
    void supportCurlyBracketsInPatterns() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "We give {{0}%}!", 1.00);
        assertThat(output).isEqualTo("We give {100}%!");
    }

    /**
     * Verifies that the {@link ChoiceFormat} syntax is supported for conditional formatting without any placeholders.
     */
    @Test
    void formatConditionalWithoutPlaceholders() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String message = "{-1#negative|0#zero|0<positive}";
        assertThat(formatter.format(configuration, message, -1)).isEqualTo("negative");
        assertThat(formatter.format(configuration, message, 0)).isEqualTo("zero");
        assertThat(formatter.format(configuration, message, +1)).isEqualTo("positive");
    }

    /**
     * Verifies that the {@link ChoiceFormat} syntax is supported for conditional formatting with a single placeholder.
     */
    @Test
    void formatConditionalWithSinglePlaceholder() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String message = "There {0#are no files|1#is one file|1<are {#,###} files}.";
        assertThat(formatter.format(configuration, message, -1)).isEqualTo("There are no files.");
        assertThat(formatter.format(configuration, message, 0)).isEqualTo("There are no files.");
        assertThat(formatter.format(configuration, message, 1)).isEqualTo("There is one file.");
        assertThat(formatter.format(configuration, message, 2)).isEqualTo("There are 2 files.");
        assertThat(formatter.format(configuration, message, 1000)).isEqualTo("There are 1,000 files.");
    }

    /**
     * Verifies that the {@link ChoiceFormat} syntax is supported for conditional formatting with multiple placeholders.
     */
    @Test
    void formatConditionalWithMultiplePlaceholders() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String message = "The call took {0#{0.00}|10#{#,###}} seconds.";
        assertThat(formatter.format(configuration, message, 0.00)).isEqualTo("The call took 0.00 seconds.");
        assertThat(formatter.format(configuration, message, 1000)).isEqualTo("The call took 1,000 seconds.");
    }

    /**
     * Verifies that there is a fallback for invalid {@link ChoiceFormat} patterns.
     */
    @Test
    void formatConditionalWithInValidPattern() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        assertThat(formatter.format(configuration, "{#|#}", 42)).isEqualTo("42");

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("#|#", "42");
        });
    }

    /**
     * Verifies that pipes can be escaped to avoid conditional formatting.
     */
    @Test
    void escapePipes() {
        EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(loader, logger);
        String output = formatter.format(configuration, "There are {'|'#,###'|'} files.", 42);
        assertThat(output).isEqualTo("There are |42| files.");
    }

}
