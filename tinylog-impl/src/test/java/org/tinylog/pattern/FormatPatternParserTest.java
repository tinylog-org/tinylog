/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.pattern;

import java.time.LocalDate;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FormatPatternParser}.
 */
public final class FormatPatternParserTest {

	private static final String NEW_LINE = System.lineSeparator();

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(false);

	/**
	 * Verifies that a plain text will be kept and output correctly.
	 */
	@Test
	public void plainText() {
		assertThat(render("Hello World!", LogEntryBuilder.empty().create())).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that {@code {date}} can be parsed and the returned token will output the date of issue.
	 */
	@Test
	public void dateWithDefaultPattern() {
		LocalDate date = LocalDate.of(1985, 06, 03);
		assertThat(render("date", LogEntryBuilder.empty().date(date).create())).contains("1985", "06", "03");
	}

	/**
	 * Verifies that {@code {date}} can be parsed with a defined pattern and the returned token will output the date of
	 * issue as defined in that pattern.
	 */
	@Test
	public void dateWithDefinedPattern() {
		LocalDate date = LocalDate.of(1985, 06, 03);
		assertThat(render("date: yyyy-MM-dd", LogEntryBuilder.empty().date(date).create())).isEqualTo("1985-06-03");
	}

	/**
	 * Verifies that a default pattern will be used, if the custom pattern for {@code {date}} is invalid.
	 */
	@Test
	public void dateWithInvalidPattern() {
		LocalDate date = LocalDate.of(1985, 06, 03);
		assertThat(render("date: inval'd", LogEntryBuilder.empty().date(date).create())).contains("1985", "06", "03");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("inval'd");
	}

	/**
	 * Verifies that {@code {timestamp}} can be parsed and the returned token will output the timestamp of issue, in seconds.
	 */
	@Test
	public void timestampWithDefaultPattern() {
		LocalDate date = LocalDate.of(1985, 06, 03);
		assertThat(render("timestamp", LogEntryBuilder.empty().date(date).create())).isEqualTo("486604800");
	}

	/**
	 * Verifies that {@code {timestamp}} can be parsed with a specified milliseconds pattern and the returned token will output the date of
	 * issue as a timestamp in milliseconds.
	 */
	@Test
	public void timestampWithMillisecondsPattern() {
		LocalDate date = LocalDate.of(1985, 06, 03);
		assertThat(render("timestamp: milliseconds", LogEntryBuilder.empty().date(date).create())).isEqualTo("486604800000");
	}

	/**
	 * Verifies that the default seconds pattern will be used, if the custom pattern for {@code {timestamp}} is invalid.
	 */
	@Test
	public void timestampWithUnknownPattern() {
		LocalDate date = LocalDate.of(1985, 06, 03);
		assertThat(render("timestamp: inval'd", LogEntryBuilder.empty().date(date).create())).isEqualTo("486604800");
	}

	/**
	 * Verifies that {@code {pid}} can be parsed and the returned token will output the process ID.
	 */
	@Test
	public void processId() {
		assertThat(render("pid", LogEntryBuilder.empty().create())).isEqualTo(Long.toString(ProcessHandle.current().pid()));
	}

	/**
	 * Verifies that {@code {thread}} can be parsed and the returned token will output the thread name.
	 */
	@Test
	public void threadName() {
		Thread thread = new Thread("My Thread");
		assertThat(render("thread", LogEntryBuilder.empty().thread(thread).create())).isEqualTo("My Thread");
	}

	/**
	 * Verifies that {@code {threadId}} can be parsed and the returned token will output the thread ID.
	 */
	@Test
	public void threadId() {
		Thread thread = Thread.currentThread();
		assertThat(render("thread-id", LogEntryBuilder.empty().thread(thread).create())).isEqualTo(Long.toString(thread.getId()));
	}

	/**
	 * Verifies that {@code {context}} can be parsed and the returned token will output the defined thread context
	 * value.
	 */
	@Test
	public void context() {
		assertThat(render("context: pi", LogEntryBuilder.empty().create())).isEmpty();
		assertThat(render("context: pi", LogEntryBuilder.empty().context("pi", "3.14").create())).isEqualTo("3.14");
	}

	/**
	 * Verifies that {@code {context}} without a defined key will produce an error.
	 */
	@Test
	public void contextMissingKey() {
		assertThat(render("context", LogEntryBuilder.empty().create())).isEmpty();
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("context");
	}

	/**
	 * Verifies that {@code {context}} can be parsed with a default value for non-existent mappings and the returned
	 * token will output the defined thread context value.
	 */

	@Test
	public void contextDefault() {
		assertThat(render("context: pi, -", LogEntryBuilder.empty().create())).isEqualTo("-");
		assertThat(render("context: pi, -", LogEntryBuilder.empty().context("pi", "3.14").create())).isEqualTo("3.14");
	}

	/**
	 * Verifies that {@code {context}} with a default value for non-existent mappings, but without a defined key will
	 * produce an error.
	 */
	@Test
	public void contextDefaultMissingKey() {
		assertThat(render("context: ,-", LogEntryBuilder.empty().create())).isEmpty();
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("context");
	}

	/**
	 * Verifies that {@code {class}} can be parsed and the returned token will output the fully-qualified class name.
	 */
	@Test
	public void fullClassName() {
		assertThat(render("class", LogEntryBuilder.empty().className("my.package.MyClass").create())).isEqualTo("my.package.MyClass");
	}

	/**
	 * Verifies that {@code {className}} can be parsed and the returned token will output the class name without
	 * package.
	 */
	@Test
	public void simpleClassName() {
		assertThat(render("class-name", LogEntryBuilder.empty().className("my.package.MyClass").create())).isEqualTo("MyClass");
	}

	/**
	 * Verifies that {@code {package}} can be parsed and the returned token will output the package name.
	 */
	@Test
	public void packageName() {
		assertThat(render("package", LogEntryBuilder.empty().className("my.package.MyClass").create())).isEqualTo("my.package");
	}

	/**
	 * Verifies that {@code {method}} can be parsed and the returned token will output the method name.
	 */
	@Test
	public void methodName() {
		assertThat(render("method", LogEntryBuilder.empty().methodName("foo").create())).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code {file}} can be parsed and the returned token will output the file name.
	 */
	@Test
	public void fileName() {
		assertThat(render("file", LogEntryBuilder.empty().fileName("MyFile.java").create())).isEqualTo("MyFile.java");
	}

	/**
	 * Verifies that {@code {line}} can be parsed and the returned token will output the source line number.
	 */
	@Test
	public void lineNumber() {
		assertThat(render("line", LogEntryBuilder.empty().lineNumber(42).create())).isEqualTo("42");
	}

	/**
	 * Verifies that {@code {tag}} can be parsed and the returned token will output the logger tag if existing.
	 */
	@Test
	public void tag() {
		assertThat(render("tag", LogEntryBuilder.empty().tag("SYSTEM").create())).isEqualTo("SYSTEM");
		assertThat(render("tag", LogEntryBuilder.empty().create())).isEmpty();
	}

	/**
	 * Verifies that {@code {tag}} can be parsed with a default value for non-existent tags and the returned token will
	 * output the logger tag if existing.
	 */
	@Test
	public void tagDefault() {
		assertThat(render("tag", LogEntryBuilder.empty().tag("SYSTEM").create())).isEqualTo("SYSTEM");
		assertThat(render("tag: -", LogEntryBuilder.empty().create())).isEqualTo("-");
	}

	/**
	 * Verifies that {@code {level}} can be parsed and the returned token will output the severity level.
	 */
	@Test
	public void level() {
		assertThat(render("level", LogEntryBuilder.empty().level(Level.DEBUG).create())).isEqualTo("DEBUG");
	}

	/**
	 * Verifies that {@code {message}} can be parsed and the returned token will output the text message as well as the
	 * exception.
	 */
	@Test
	public void message() {
		Exception exception = new NullPointerException();
		assertThat(render("message", LogEntryBuilder.empty().message("Hello World!").exception(exception).create()))
			.startsWith("Hello World!")
			.contains(NullPointerException.class.getName())
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that {@code {messageOnly}} can be parsed and the returned token will output the text message, but not
	 * the exception.
	 */
	@Test
	public void messageOnly() {
		Exception exception = new NullPointerException();
		assertThat(render("message-only", LogEntryBuilder.empty().message("Hello World!").exception(exception).create()))
			.isEqualTo("Hello World!");
	}

	/**
	 * Verifies that {@code {exception}} can be parsed and the returned token will output the exception.
	 */
	@Test
	public void exception() {
		Exception exception = new NullPointerException();
		assertThat(render("exception", LogEntryBuilder.empty().exception(exception).create()))
			.contains(NullPointerException.class.getName())
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that {@code {opening-curly-bracket"}} can be parsed and outputs a single opening curly bracket '{'.
	 */
	@Test
	public void openingCurlyBracket() {
		assertThat(render("opening-curly-bracket", LogEntryBuilder.empty().create())).isEqualTo("{");
	}

	/**
	 * Verifies that {@code {closing-curly-bracket"}} can be parsed and outputs a single closing curly bracket '}'.
	 */
	@Test
	public void closingCurlyBracket() {
		assertThat(render("closing-curly-bracket", LogEntryBuilder.empty().create())).isEqualTo("}");
	}

	/**
	 * Verifies that {@code {pipe}} can be parsed and outputs a single vertical bar '|'.
	 */
	@Test
	public void pipe() {
		assertThat(render("pipe", LogEntryBuilder.empty().create())).isEqualTo("|");
	}

	/**
	 * Verifies that {@code {any | min-size=X}} can be parsed and the returned token will apply minimum size.
	 */
	@Test
	public void minimumSize() {
		assertThat(render("{level | min-size=6}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO  ");
	}

	/**
	 * Verifies that {@code {{any}:|min-size=X}} can be parsed and the returned token will apply minimum size.
	 */
	@Test
	public void nestedMinimumSize() {
		assertThat(render("{{level}:|min-size=6}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO: ");
	}

	/**
	 * Verifies that invalid minimum size values will produce an error.
	 */
	@Test
	public void invalidMinimumSize() {
		assertThat(render("{level | min-size=-1}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO");
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce("min-size")
			.containsOnlyOnce("-1");
	}

	/**
	 * Verifies that {@code {any | indent=X}} can be parsed and the returned token will apply indentation.
	 */
	@Test
	public void indentation() {
		assertThat(render("{message | indent=2}", LogEntryBuilder.empty().message("12" + NEW_LINE + "3").create()))
			.isEqualTo("12" + NEW_LINE + "  3");
	}

	/**
	 * Verifies that invalid indentation values will produce an error.
	 */
	@Test
	public void invalidIndentation() {
		assertThat(render("{level | indent=ABC}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO");
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce("indent")
			.containsOnlyOnce("ABC");
	}

	/**
	 * Verifies that a combination of multiple placeholders can be parsed and the returned token will output the
	 * expected values.
	 */
	@Test
	public void combined() {
		assertThat(render("<{file}/{message}>", LogEntryBuilder.empty().fileName("MyFile.java").message("Hello World!").create()))
			.isEqualTo("<MyFile.java/Hello World!>");
	}

	/**
	 * Verifies that a nested placeholder can be parsed and the returned token will output the expected value.
	 */
	@Test
	public void nested() {
		assertThat(render("{{message}}", LogEntryBuilder.empty().message("Hello World!").create())).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a missing opening curly bracket will produce an error.
	 */
	@Test
	public void missingOpeningCurlyBracket() {
		assertThat(render("message}", LogEntryBuilder.empty().message("Hello World!").create())).isEqualTo("message}");
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsIgnoringCase("opening curly bracket")
			.containsOnlyOnce("message}");
	}

	/**
	 * Verifies that a missing closing curly bracket will produce an error.
	 */
	@Test
	public void missingClosingCurlyBracket() {
		assertThat(render("{message", LogEntryBuilder.empty().message("Hello World!").create())).isEqualTo("{message");
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsIgnoringCase("closing curly bracket")
			.containsOnlyOnce("{message");
	}

	/**
	 * Verifies that missing values for a style option will produce an error.
	 */
	@Test
	public void missingStyleOptionValue() {
		assertThat(render("{level | min-size}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO");
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce("min-size");
	}

	/**
	 * Verifies that unknown style options will produce an error.
	 */
	@Test
	public void unknownStyleOption() {
		assertThat(render("{level | test=42}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO");
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce("test");
	}

	/**
	 * Parses a pattern and renders the returned token afterwards.
	 *
	 * @param pattern
	 *            Pattern to parse
	 * @param entry
	 *            Log entry for rendering the produced token
	 * @return Render result of produced token
	 */
	private String render(final String pattern, final LogEntry entry) {
		Token token = FormatPatternParser.parse(pattern);
		if (token == null) {
			return null;
		} else {
			StringBuilder builder = new StringBuilder();
			token.render(entry, builder);
			return builder.toString();
		}
	}

}
