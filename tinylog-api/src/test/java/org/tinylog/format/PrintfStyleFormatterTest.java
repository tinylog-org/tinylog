/*
 * Copyright 2020 Martin Winandy
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

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.Supplier;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PrintfStyleFormatter}.
 */
public final class PrintfStyleFormatterTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that a printf style message without any format specifiers will be returned unchanged.
	 */
	@Test
	public void withoutPlaceholders() {
		assertThat(format("Hello World!")).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a format specifier without any context text will be resolved.
	 */
	@Test
	public void onlyPlaceholder() {
		assertThat(format("%d", 42)).isEqualTo("42");
	}

	/**
	 * Verifies that a single format specifier will be resolved.
	 */
	@Test
	public void singlePlaceholder() {
		assertThat(format("Hello %s!", "tinylog")).isEqualTo("Hello tinylog!");
	}

	/**
	 * Verifies that multiple format specifiers will be resolved in the correct order.
	 */
	@Test
	public void multiplePlaceholders() {
		assertThat(format("%d + %d = %d", 1, 2, 3)).isEqualTo("1 + 2 = 3");
	}

	/**
	 * Verifies that lazy argument suppliers can be evaluated.
	 *
	 * @see Supplier
	 */
	@Test
	public void lazyArgumentSupplier() {
		Supplier<Integer> supplier = () -> 42;
		assertThat(format("It is %d", supplier)).isEqualTo("It is 42");
	}

	/**
	 * Verifies that printf style messages with more arguments than format specifiers can be handled.
	 */
	@Test
	public void tooManyArguments() {
		assertThat(format("Hello %s!", "tinylog", "world")).isEqualTo("Hello tinylog!");
	}

	/**
	 * Verifies that prinf style messages with less arguments than format specifiers will be reported.
	 */
	@Test
	public void tooFewArguments() {
		assertThat(format("Hello %s!")).isEqualTo("Hello %s!");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("Hello %s!");
	}

	/**
	 * Verifies that prinf style messages with illegal format specifiers will be reported.
	 */
	@Test
	public void invalidFormatSpecifier() {
		assertThat(format("Hello %!", "tinylog")).isEqualTo("Hello %!");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("Hello %!");
	}

	/**
	 * Uses {@link PrintfStyleFormatter} for formatting a printf style message.
	 *
	 * @param message
	 *            Printf style message with format specifiers
	 * @param arguments
	 *            Values for format specifiers
	 * @return Formatted text message
	 */
	private static String format(final String message, final Object... arguments) {
		return format(Locale.ROOT, message, arguments);
	}

	/**
	 * Uses {@link PrintfStyleFormatter} for formatting a printf style message.
	 *
	 * @param locale
	 *            Locale for formatting numbers and dates
	 * @param message
	 *            Printf style message with format specifiers
	 * @param arguments
	 *            Values for format specifiers
	 * @return Formatted text message
	 */
	private static String format(final Locale locale, final String message, final Object... arguments) {
		return new PrintfStyleFormatter(locale).format(message, arguments);
	}

}
