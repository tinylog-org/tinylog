/*
 * Copyright 2017 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.format;

import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.tinylog.Supplier;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * Tests for {@link AdvancedMessageFormatter}.
 */
@RunWith(Parameterized.class)
public final class AdvancedMessageFormatterTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	private final boolean escape;

	/**
	 * @param escape
	 *            {@code true} to enable escaping by ticks, {@code false} to disable
	 */
	public AdvancedMessageFormatterTest(final boolean escape) {
		this.escape = escape;
	}

	/**
	 * Gets both escape states ({@code true} and {@code false}.
	 *
	 * @return Both escape states
	 */
	@Parameterized.Parameters(name = "escape = {0}")
	public static Collection<Object[]> getEscapeStates() {
		List<Object[]> states = new ArrayList<>();
		states.add(new Object[] { true });
		states.add(new Object[] { false });
		return states;
	}

	/**
	 * Verifies that a text message without any placeholders will be returned unchanged.
	 */
	@Test
	public void withoutPlaceholders() {
		assertThat(format("Hello World!")).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a placeholder without any context text will be replaced.
	 */
	@Test
	public void onlyPlaceholder() {
		assertThat(format("{}", 42)).isEqualTo("42");
	}

	/**
	 * Verifies that a single placeholder will be replaced.
	 */
	@Test
	public void singlePlaceholder() {
		assertThat(format("Hello {}!", "tinylog")).isEqualTo("Hello tinylog!");
	}

	/**
	 * Verifies that multiple placeholders will be replaced in the correct order.
	 */
	@Test
	public void multiplePlaceholders() {
		assertThat(format("{} + {} = {}", 1, 2, 3)).isEqualTo("1 + 2 = 3");
	}

	/**
	 * Verifies that lazy argument suppliers can be evaluated.
	 *
	 * @see Supplier
	 */
	@Test
	public void lazyArgumentSupplier() {
		Supplier<Integer> supplier = () -> 42;
		assertThat(format("It is {}", supplier)).isEqualTo("It is 42");
	}

	/**
	 * Verifies that {@link ChoiceFormat} compatible patterns are supported.
	 */
	@Test
	public void choiceFormat() {
		assertThat(format("{0#zero|1#one|1<multiple}", 0)).isEqualTo("zero");
		assertThat(format("{0#zero|1#one|1<multiple}", 1)).isEqualTo("one");
		assertThat(format("{0#zero|1#one|1<multiple}", 2)).isEqualTo("multiple");
	}

	/**
	 * Verifies that {@link NumberFormat} compatible patterns are supported.
	 */
	@Test
	public void numberFormat() {
		assertThat(format(Locale.US, "{0.00}", 1)).isEqualTo("1.00");
		assertThat(format(Locale.GERMANY, "{0.00}", 1)).isEqualTo("1,00");
	}

	/**
	 * Verifies that a single {@link NumberFormat} compatible pattern can be used in {@link ChoiceFormat} patterns.
	 */
	@Test
	public void choiceAndSingleNumberFormat() {
		assertThat(format("{0#zero|1#one|1<{000}}", 0)).isEqualTo("zero");
		assertThat(format("{0#zero|1#one|1<{000}}", 42)).isEqualTo("042");
	}

	/**
	 * Verifies that multiple different {@link NumberFormat} compatible patterns can be used in {@link ChoiceFormat}
	 * patterns.
	 */
	@Test
	public void choiceAndMultipleNumberFormats() {
		assertThat(format("{0#{0.00}|10#{#,###}}", 0)).isEqualTo("0.00");
		assertThat(format("{0#{0.00}|10#{#,###}}", 1000)).isEqualTo("1,000");
	}

	/**
	 * Verifies that text messages with more arguments than placeholders can be handled.
	 */
	@Test
	public void tooManyArguments() {
		assertThat(format("Hello {}!", "tinylog", "world")).isEqualTo("Hello tinylog!");
	}

	/**
	 * Verifies that text messages with less arguments than placeholders can be handled.
	 */
	@Test
	public void tooFewArguments() {
		assertThat(format("Hello {}!")).isEqualTo("Hello {}!");
	}

	/**
	 * Verifies that placeholders with a missing opening curly bracket can be handled.
	 */
	@Test
	public void missingOpeningCurlyBracket() {
		assertThat(format("} Hello", "tinylog")).isEqualTo("} Hello");
		assertThat(format("Hello }", "tinylog")).isEqualTo("Hello }");
	}

	/**
	 * Verifies that placeholders with a missing closing curly bracket can be handled.
	 */
	@Test
	public void missingClosingCurlyBracket() {
		assertThat(format("{ Hello", "tinylog")).isEqualTo("{ Hello");
		assertThat(format("Hello {", "tinylog")).isEqualTo("Hello {");
	}

	/**
	 * Verifies that placeholders can be escaped, if escaping is enabled.
	 */
	@Test
	public void ignoreEscapedPlaceholders() {
		assumeThat(escape).isTrue(); // Escaping enabled
		assertThat(format("'{foo}' {}", "bar")).isEqualTo("{foo} bar");
	}

	/**
	 * Verifies that a placeholder within ticks can be resolved, if escaping is disabled.
	 */
	@Test
	public void resolvePlaceholderWithinTicks() {
		assumeThat(escape).isFalse(); // Escaping enabled
		assertThat(format("'{}'", "foo")).isEqualTo("'foo'");
	}

	/**
	 * Verifies that double ticks will be converted into a single tick, if escaping is enabled.
	 */
	@Test
	public void convertDoubleTicks() {
		assumeThat(escape).isTrue(); // Escaping enabled
		assertThat(format("this <''> is a single tick")).isEqualTo("this <'> is a single tick");
	}

	/**
	 * Verifies that double ticks will be kept as double ticks, if escaping is disabled.
	 */
	@Test
	public void keepDoubleTicks() {
		assumeThat(escape).isFalse(); // Escaping disabled
		assertThat(format("these <''> are double ticks")).isEqualTo("these <''> are double ticks");
	}

	/**
	 * Verifies that single ticks will be output.
	 */
	@Test
	public void keepSingleTicks() {
		assertThat(format("this <'> is a single tick")).isEqualTo("this <'> is a single tick");
	}

	/**
	 * Verifies that illegal {@link ChoiceFormat} patterns will be reported.
	 */
	@Test
	public void illegalChoiceFormat() {
		assertThat(format("Test {#|}!", 42)).isEqualTo("Test 42!");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("#|");
	}

	/**
	 * Verifies that illegal {@link NumberFormat} patterns will be reported.
	 */
	@Test
	public void illegalNumberFormat() {
		assertThat(format("Test {#..#}!", 42)).isEqualTo("Test 42!");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("#..#");
	}

	/**
	 * Uses {@link AdvancedMessageFormatter} for formatting a text message.
	 *
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	private String format(final String message, final Object... arguments) {
		return format(Locale.ROOT, message, arguments);
	}

	/**
	 * Uses {@link AdvancedMessageFormatter} for formatting a text message.
	 *
	 * @param locale
	 *            Locale for formatting numbers
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	private String format(final Locale locale, final String message, final Object... arguments) {
		return new AdvancedMessageFormatter(locale, escape).format(message, arguments);
	}

}
