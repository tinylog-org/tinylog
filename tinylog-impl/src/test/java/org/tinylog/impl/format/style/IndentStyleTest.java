package org.tinylog.impl.format.style;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.impl.format.placeholders.BundlePlaceholder;
import org.tinylog.impl.format.placeholders.Placeholder;
import org.tinylog.impl.format.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;

class IndentStyleTest {

	/**
	 * Verifies that a given text is indented as expected.
	 *
	 * @param input The input text to indent
	 * @param expected The expected result after indentation
	 */
	@ParameterizedTest
	@CsvSource({
		"''          , ''            ",
		"'foo'       , ' foo'        ",
		"'foo\t'     , ' foo\t'      ",
		"'\tfoo'     , '  foo'       ",
		"'\t\tfoo'   , '   foo'      ",
		"'foo\nbar'  , ' foo\n bar'  ",
		"'foo\n\tbar', ' foo\n  bar' ",
		"'foo\n\nbar', ' foo\n \n bar'"
	})
	void applyEntireText(String input, String expected) {
		Placeholder placeholder = new IndentStyle(new StaticTextPlaceholder(normalize(input)), " ");
		assertThat(render(placeholder)).isEqualTo(normalize(expected));
	}

	/**
	 * Verifies that "foo" as actual text is indented as expected after a given prefix.
	 *
	 * @param prefix The prefix to place in front of the actual text "foo"
	 * @param expected The expected result after indentation
	 */
	@ParameterizedTest
	@CsvSource({
		"'1'   , '1foo'    ",
		"'12'  , '12foo'   ",
		"'\n'  , '\n foo'  ",
		"'1\n' , '1\n foo' ",
		"'12\n', '12\n foo'"
	})
	void applyPrefixedText(String prefix, String expected) {
		List<Placeholder> placeholders = ImmutableList.of(
			new StaticTextPlaceholder(normalize(prefix)),
			new IndentStyle(new StaticTextPlaceholder("foo"), " ")
		);
		Placeholder bundle = new BundlePlaceholder(placeholders);
		assertThat(render(bundle)).isEqualTo(normalize(expected));
	}

	/**
	 * Replaces Unix new lines ("\n") with new lines of the actual operating system.
	 *
	 * @param text The text in which the new lines should be replaced
	 * @return The passed text with new lines of the actual operating system instead of Unix new lines
	 */
	private static String normalize(String text) {
		return text.replace("\n", System.lineSeparator());
	}

	/**
	 * Renders the passed placeholder.
	 *
	 * @param placeholder The placeholder to render
	 * @return The output of the passed placeholder
	 */
	private static String render(Placeholder placeholder) {
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		return renderer.render(new LogEntryBuilder().create());
	}

}
