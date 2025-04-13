package org.tinylog.core.format.message;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class SimpleMessageFormatterTest {

    @Inject
    private Configuration configuration;

    /**
     * Verifies that a single static argument can be formatted.
     */
    @Test
    void resolveStaticSingleArgument() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(configuration, "Hello {}!", "Alice");
        assertThat(output).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that a single lazy argument can be formatted.
     */
    @Test
    void formatLazyArgument() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        Supplier<?> supplier = () -> "Alice";
        String output = formatter.format(configuration, "Hello {}!", supplier);
        assertThat(output).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that multiple arguments can be formatted.
     */
    @Test
    void resolveMultipleArguments() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(configuration, "{} + {} = {}", 1, 2, 3);
        assertThat(output).isEqualTo("1 + 2 = 3");
    }

    /**
     * Verifies that placeholders without matching arguments are silently ignored.
     */
    @Test
    void ignoreSuperfluousPlaceholders() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(configuration, "{}, {}, and {}", 1, 2);
        assertThat(output).isEqualTo("1, 2, and {}");
    }

    /**
     * Verifies that superfluous arguments are silently ignored.
     */
    @Test
    void ignoreSuperfluousArguments() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(configuration, "{}, {}, and {}", 1, 2, 3, 4);
        assertThat(output).isEqualTo("1, 2, and 3");
    }

}
