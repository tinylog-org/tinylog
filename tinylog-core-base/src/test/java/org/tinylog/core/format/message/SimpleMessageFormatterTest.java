package org.tinylog.core.format.message;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class SimpleMessageFormatterTest {

    @Inject
    private Framework framework;

    /**
     * Verifies that a single argument can be resolved.
     */
    @Test
    void resolveSingleArgument() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(framework, "Hello {}!", "Alice");
        assertThat(output).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that multiple arguments can be resolved.
     */
    @Test
    void resolveMultipleArguments() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(framework, "{} + {} = {}", 1, 2, 3);
        assertThat(output).isEqualTo("1 + 2 = 3");
    }

    /**
     * Verifies that placeholders without matching arguments are silently ignored.
     */
    @Test
    void ignoreSuperfluousPlaceholders() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(framework, "{}, {}, and {}", 1, 2);
        assertThat(output).isEqualTo("1, 2, and {}");
    }

    /**
     * Verifies that superfluous arguments are silently ignored.
     */
    @Test
    void ignoreSuperfluousArguments() {
        SimpleMessageFormatter formatter = new SimpleMessageFormatter();
        String output = formatter.format(framework, "{}, {}, and {}", 1, 2, 3, 4);
        assertThat(output).isEqualTo("1, 2, and 3");
    }

}
