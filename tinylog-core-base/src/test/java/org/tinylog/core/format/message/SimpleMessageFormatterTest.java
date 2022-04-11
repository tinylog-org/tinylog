package org.tinylog.core.format.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleMessageFormatterTest {

	/**
	 * Verifies that a single argument can be resolved.
	 */
	@Test
	void resolveSingleArgument() {
		SimpleMessageFormatter formatter = new SimpleMessageFormatter();
		String output = formatter.format("Hello {}!", "Alice");
		assertThat(output).isEqualTo("Hello Alice!");
	}

	/**
	 * Verifies that multiple arguments can be resolved.
	 */
	@Test
	void resolveMultipleArguments() {
		SimpleMessageFormatter formatter = new SimpleMessageFormatter();
		String output = formatter.format("{} + {} = {}", 1, 2, 3);
		assertThat(output).isEqualTo("1 + 2 = 3");
	}

	/**
	 * Verifies that placeholders without matching arguments are silently ignored.
	 */
	@Test
	void ignoreSuperfluousPlaceholders() {
		SimpleMessageFormatter formatter = new SimpleMessageFormatter();
		String output = formatter.format("{}, {}, and {}", 1, 2);
		assertThat(output).isEqualTo("1, 2, and {}");
	}

	/**
	 * Verifies that superfluous arguments are silently ignored.
	 */
	@Test
	void ignoreSuperfluousArguments() {
		SimpleMessageFormatter formatter = new SimpleMessageFormatter();
		String output = formatter.format("{}, {}, and {}", 1, 2, 3, 4);
		assertThat(output).isEqualTo("1, 2, and 3");
	}

}
