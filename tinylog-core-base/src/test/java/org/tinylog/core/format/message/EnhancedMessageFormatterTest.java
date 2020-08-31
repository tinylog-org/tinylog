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

package org.tinylog.core.format.message;

import java.text.ChoiceFormat;
import java.time.LocalTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;

import static org.assertj.core.api.Assertions.assertThat;

class EnhancedMessageFormatterTest {

	private final Framework framework = new Framework(false, false);

	/**
	 * Verifies that a string can be formatted without defining a format pattern.
	 */
	@Test
	void formatDefaultStringWithoutPattern() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("Hello {}!", "Alice");
		assertThat(output).isEqualTo("Hello Alice!");
	}

	/**
	 * Verifies that format patterns are silently ignored for strings.
	 */
	@Test
	void ignorePatternForStrings() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("Hello {###}!", "Alice");
		assertThat(output).isEqualTo("Hello Alice!");
	}

	/**
	 * Verifies that a number can be formatted without defining a format pattern.
	 */
	@Test
	void formatNumberWithoutPattern() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("The maximum port number is {}.", 65535);
		assertThat(output).isEqualTo("The maximum port number is 65535.");
	}

	/**
	 * Verifies that a number can be formatted with a format pattern.
	 */
	@Test
	void formatNumberWithPattern() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("Pi is {0.00}", Math.PI);
		assertThat(output).isEqualTo("Pi is 3.14");
	}

	/**
	 * Verifies that a local time can be formatted with a format pattern.
	 */
	@Test
	void formatTimeWithPattern() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("It is {hh:mm a}.", LocalTime.of(12, 30));
		assertThat(output).isEqualTo("It is 12:30 PM.");
	}

	/**
	 * Verifies that multiple arguments can be formatted.
	 */
	@Test
	void formatMultipleArguments() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("{} + {} = {}", 1, 2, 3);
		assertThat(output).isEqualTo("1 + 2 = 3");
	}

	/**
	 * Verifies that placeholders without matching arguments are silently ignored.
	 */
	@Test
	void ignoreSuperfluousPlaceholders() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("{}, {}, and {}", 1, 2);
		assertThat(output).isEqualTo("1, 2, and {}");
	}

	/**
	 * Verifies that superfluous arguments are silently ignored.
	 */
	@Test
	void ignoreSuperfluousArguments() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("{}, {}, and {}", 1, 2, 3, 4);
		assertThat(output).isEqualTo("1, 2, and 3");
	}

	/**
	 * Verifies that invalid format patterns are silently ignored.
	 */
	@Test
	void ignoreInvalidPatterns() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("<{0 # 0}>", 42);
		assertThat(output).isEqualTo("<42>");
	}

	/**
	 * Verifies that one single quote can be output.
	 */
	@Test
	void keepSingleQuote() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("It is {} o'clock.", "twelve");
		assertThat(output).isEqualTo("It is twelve o'clock.");
	}

	/**
	 * Verifies that two directly consecutive singe quotes are output as one single quote.
	 */
	@Test
	void convertSingleQuote() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("It is {} o''clock.", "twelve");
		assertThat(output).isEqualTo("It is twelve o'clock.");
	}

	/**
	 * Verifies that curly brackets can be escaped by single quotes.
	 */
	@Test
	void escapeCurlyBrackets() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("Brackets can be escaped ('{}') or replaced ({})", 42);
		assertThat(output).isEqualTo("Brackets can be escaped ({}) or replaced (42)");
	}

	/**
	 * Verifies that phrases in format patterns can be escaped by single quotes.
	 */
	@Test
	void escapePhraseInPatterns() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("It is {hh 'o''clock'}.", LocalTime.of(12, 0));
		assertThat(output).isEqualTo("It is 12 o'clock.");
	}

	/**
	 * Verifies that an oping curly bracket is output unmodified if there is no corresponding closing curly bracket.
	 */
	@Test
	void ignoreOpeningCurlyBracketWithoutClosingBracket() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("Here is a mistake: <{>", 42);
		assertThat(output).isEqualTo("Here is a mistake: <{>");
	}

	/**
	 * Verifies that curly brackets can be nested and used as part of a format pattern.
	 */
	@Test
	void supportCurlyBracketsInPatterns() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("We give {{0}%}!", 1.00);
		assertThat(output).isEqualTo("We give {100}%!");
	}

	/**
	 * Verifies that the {@link ChoiceFormat} syntax is supported for conditional formatting.
	 */
	@Test
	void formatConditional() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String message = "There {0#are no files|1#is one file|1<are {#,###} files}.";
		assertThat(formatter.format(message, -1)).isEqualTo("There are no files.");
		assertThat(formatter.format(message, 0)).isEqualTo("There are no files.");
		assertThat(formatter.format(message, 1)).isEqualTo("There is one file.");
		assertThat(formatter.format(message, 2)).isEqualTo("There are 2 files.");
		assertThat(formatter.format(message, 1000)).isEqualTo("There are 1,000 files.");
	}

	/**
	 * Verifies that pipes can be escaped to avoid conditional formatting.
	 */
	@Test
	void escapePipes() {
		EnhancedMessageFormatter formatter = new EnhancedMessageFormatter(framework, Locale.US);
		String output = formatter.format("There are {'|'#,###'|'} files.", 42);
		assertThat(output).isEqualTo("There are |42| files.");
	}

}
