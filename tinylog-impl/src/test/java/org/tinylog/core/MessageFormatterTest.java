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

package org.tinylog.core;

import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MessageFormatter}.
 */
public final class MessageFormatterTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

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
	 * Verifies that {@link NumberFormat} compatible patterns can be used in {@link ChoiceFormat} patterns.
	 */
	@Test
	public void choiceAndnumberFormat() {
		assertThat(format("{0#zero|1#one|1<{000}}", 0)).isEqualTo("zero");
		assertThat(format("{0#zero|1#one|1<{000}}", 42)).isEqualTo("042");
	}

	/**
	 * Verifies that text messages with more arguments than placeholders can be handled.
	 */
	@Test
	public void tooMuchArguments() {
		assertThat(format("Hello {}!", "tinylog", "world")).isEqualTo("Hello tinylog!");
	}

	/**
	 * Verifies that text messages with less arguments than placeholders can be handled.
	 */
	@Test
	public void tooLittleArguments() {
		assertThat(format("Hello {}!")).containsSequence("Hello", "!");
	}

	/**
	 * Verifies that placeholders with a missing open brace can be handled.
	 */
	@Test
	public void missingOpenBrace() {
		assertThat(format("Hello }!", "tinylog")).containsSequence("Hello", "!");
	}

	/**
	 * Verifies that placeholders with a missing close brace can be handled.
	 */
	@Test
	public void missingCloseBrace() {
		assertThat(format("Hello {!", "tinylog")).containsSequence("Hello", "!");
	}

	/**
	 * Verifies that illegal {@link ChoiceFormat} patterns will be reported.
	 */
	@Test
	public void illegalChoiceFormat() {
		assertThat(format("Test {#|}!", 42)).isEqualTo("Test 42!");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARNING").containsOnlyOnce("#|");
	}

	/**
	 * Verifies that illegal {@link NumberFormat} patterns will be reported.
	 */
	@Test
	public void illegalNumberFormat() {
		assertThat(format("Test {#..#}!", 42)).isEqualTo("Test 42!");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARNING").containsOnlyOnce("#..#");
	}

	/**
	 * Uses {@link MessageFormatter} for formatting a text message.
	 *
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	private static String format(final String message, final Object... arguments) {
		return format(Locale.ROOT, message, arguments);
	}

	/**
	 * Uses {@link MessageFormatter} for formatting a text message.
	 *
	 * @param locale
	 *            Locale for formatting numbers
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	private static String format(final Locale locale, final String message, final Object... arguments) {
		return new MessageFormatter(locale).format(message, arguments);
	}

}
