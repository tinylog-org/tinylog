package org.tinylog.impl.format.styles;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class MaxTextLengthStyleTest {

	/**
	 * Verifies that the max length style is applied to input strings as expected.
	 *
	 * @param input The source string to which the max length style should be applied
	 * @param maxLength The maximum length for the input string
	 * @param expected The expected result after applying the max length style
	 */
	@ParameterizedTest
	@CsvSource({
		/* two letters input   */
		"'ab'   , 1, 'a'    ",
		"'ab'   , 2, 'ab'   ",
		"'ab'   , 3, 'ab'   ",
		/* three letters input */
		"'abc'  , 1, 'a'    ",
		"'abc'  , 2, 'ab'   ",
		"'abc'  , 3, 'abc'  ",
		"'abc'  , 4, 'abc'  ",
		/* four letters input  */
		"'abcd' , 1, 'a'    ",
		"'abcd' , 2, 'ab'   ",
		"'abcd' , 3, '...'  ",
		"'abcd' , 4, 'abcd' ",
		"'abcd' , 5, 'abcd' ",
		/* five letters input  */
		"'abcde', 1, 'a'    ",
		"'abcde', 2, 'ab'   ",
		"'abcde', 3, '...'  ",
		"'abcde', 4, 'a...' ",
		"'abcde', 5, 'abcde'",
		"'abcde', 6, 'abcde'"
	})
	void apply(String input, int maxLength, String expected) {
		MaxTextLengthStyle style = new MaxTextLengthStyle(new StaticTextPlaceholder(input), maxLength);
		PlaceholderRenderer renderer = new PlaceholderRenderer(style);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo(expected);
	}

}
