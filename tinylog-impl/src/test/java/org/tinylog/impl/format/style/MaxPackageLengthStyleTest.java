package org.tinylog.impl.format.style;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.placeholders.PackagePlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class MaxPackageLengthStyleTest {

	/**
	 * Verifies that the max length style is applied to packages as expected.
	 *
	 * @param packageName The package name to which the max length style should be applied
	 * @param maxLength The maximum length for the output of the passed package name
	 * @param expected The expected result after applying the max length style
	 */
	@ParameterizedTest
	@CsvSource({
		/* Legal package name */
		"'org.tinylog.impl.style', 22, 'org.tinylog.impl.style'",
		"'org.tinylog.impl.style', 21, 'o.tinylog.impl.style'  ",
		"'org.tinylog.impl.style', 20, 'o.tinylog.impl.style'  ",
		"'org.tinylog.impl.style', 19, 'o.t.impl.style'        ",
		"'org.tinylog.impl.style', 14, 'o.t.impl.style'        ",
		"'org.tinylog.impl.style', 13, 'o.t.i.style'           ",
		"'org.tinylog.impl.style', 11, 'o.t.i.style'           ",
		"'org.tinylog.impl.style', 10, 'o.t.i.s'               ",
		"'org.tinylog.impl.style',  7, 'o.t.i.s'               ",
		"'org.tinylog.impl.style',  6, '....s'                 ",
		"'org.tinylog.impl.style',  5, '....s'                 ",
		"'org.tinylog.impl.style',  4, '...'                   ",
		"'org.tinylog.impl.style',  3, '...'                   ",
		"'org.tinylog.impl.style',  2, 'o.'                    ",
		"'org.tinylog.impl.style',  1, 'o'                     ",
		"'org.tinylog.impl.style',  0, ''                      ",
		/* Illegal package name with leading dot */
		"'.com.foo'              ,  8, '.com.foo'              ",
		"'.com.foo'              ,  7, '.c.foo'                ",
		"'.com.foo'              ,  6, '.c.foo'                ",
		"'.com.foo'              ,  5, '.c.f'                  ",
		"'.com.foo'              ,  4, '.c.f'                  ",
		"'.com.foo'              ,  3, '...'                   ",
		"'.com.foo'              ,  2, '.c'                    ",
		"'.com.foo'              ,  1, '.'                     ",
		"'.com.foo'              ,  0, ''                      ",
		/* Illegal package name with double dot */
		"'com..foo'              ,  8, 'com..foo'              ",
		"'com..foo'              ,  7, 'c..foo'                ",
		"'com..foo'              ,  6, 'c..foo'                ",
		"'com..foo'              ,  5, 'c..f'                  ",
		"'com..foo'              ,  4, 'c..f'                  ",
		"'com..foo'              ,  3, '...'                   ",
		"'com..foo'              ,  2, 'c.'                    ",
		"'com..foo'              ,  1, 'c'                     ",
		"'com..foo'              ,  0, ''                      ",
		/* Illegal package name with trailing dot */
		"'com.foo.'              ,  8, 'com.foo.'              ",
		"'com.foo.'              ,  7, 'c.foo.'                ",
		"'com.foo.'              ,  6, 'c.foo.'                ",
		"'com.foo.'              ,  5, 'c.f.'                  ",
		"'com.foo.'              ,  4, 'c.f.'                  ",
		"'com.foo.'              ,  3, '...'                   ",
		"'com.foo.'              ,  2, 'c.'                    ",
		"'com.foo.'              ,  1, 'c'                     ",
		"'com.foo.'              ,  0, ''                      "
	})
	void apply(String packageName, int maxLength, String expected) {
		MaxPackageLengthStyle style = new MaxPackageLengthStyle(new PackagePlaceholder(), maxLength);
		PlaceholderRenderer renderer = new PlaceholderRenderer(style);
		LogEntry logEntry = new LogEntryBuilder().className(packageName + ".Foo").create();
		assertThat(renderer.render(logEntry)).isEqualTo(expected);
	}

}
