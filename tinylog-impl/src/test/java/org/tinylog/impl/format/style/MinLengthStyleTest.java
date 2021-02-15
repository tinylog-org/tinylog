package org.tinylog.impl.format.style;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class MinLengthStyleTest {

	/**
	 * Verifies that the min length style is applied to input strings as expected.
	 *
	 * @param input The source string to which the min length style should be applied
	 * @param minLength The minimum length for the input string
	 * @param position The position for the input string
	 * @param expected The expected result after applying the min length style
	 */
	@ParameterizedTest
	@CsvSource({
		"'foo', 2, LEFT  , 'foo'  ",
		"'foo', 5, LEFT  , 'foo  '",
		"'foo', 2, CENTER, 'foo'  ",
		"'foo', 5, CENTER, ' foo '",
		"'foo', 2, RIGHT , 'foo'  ",
		"'foo', 5, RIGHT , '  foo'"
	})
	void apply(String input, int minLength, Position position, String expected) {
		MinLengthStyle style = new MinLengthStyle(new StaticTextPlaceholder(input), minLength, position);
		PlaceholderRenderer renderer = new PlaceholderRenderer(style);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo(expected);
	}

}
