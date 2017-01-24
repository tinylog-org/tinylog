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

import java.lang.management.ManagementFactory;
import java.sql.Date;
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

	private static final String NEW_LINE = System.getProperty("line.separator");

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
	 * Verifies that <tt>{date}</tt> can be parsed and the returned token will output the date of issue.
	 */
	@Test
	public void dateWithDefaultPattern() {
		Date date = Date.valueOf(LocalDate.of(1985, 06, 03));
		assertThat(render("date", LogEntryBuilder.empty().date(date).create())).contains("1985", "06", "03");
	}

	/**
	 * Verifies that <tt>{date}</tt> can be parsed with a defined pattern and the returned token will output the date of
	 * issue as defined in that pattern.
	 */
	@Test
	public void dateWithDefinedPattern() {
		Date date = Date.valueOf(LocalDate.of(1985, 06, 03));
		assertThat(render("date: yyyy-MM-dd", LogEntryBuilder.empty().date(date).create())).isEqualTo("1985-06-03");
	}

	/**
	 * Verifies that a default pattern will be used, if the custom pattern for <tt>{date}</tt> is invalid.
	 */
	@Test
	public void dateWithInvalidPattern() {
		Date date = Date.valueOf(LocalDate.of(1985, 06, 03));
		assertThat(render("date: inval'd", LogEntryBuilder.empty().date(date).create())).contains("1985", "06", "03");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("inval'd");
	}

	/**
	 * Verifies that <tt>{pid}</tt> can be parsed and the returned token will output the process ID.
	 */
	@Test
	public void processId() {
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		assertThat(render("pid", LogEntryBuilder.empty().create())).isEqualTo(pid);
	}

	/**
	 * Verifies that <tt>{thread}</tt> can be parsed and the returned token will output the thread name.
	 */
	@Test
	public void threadName() {
		Thread thread = new Thread("My Thread");
		assertThat(render("thread", LogEntryBuilder.empty().thread(thread).create())).isEqualTo("My Thread");
	}

	/**
	 * Verifies that <tt>{threadId}</tt> can be parsed and the returned token will output the thread ID.
	 */
	@Test
	public void threadId() {
		Thread thread = Thread.currentThread();
		assertThat(render("threadId", LogEntryBuilder.empty().thread(thread).create())).isEqualTo(Long.toString(thread.getId()));
	}

	/**
	 * Verifies that <tt>{context}</tt> can be parsed and the returned token will output the defined thread context
	 * value.
	 */
	@Test
	public void context() {
		assertThat(render("context: pi", LogEntryBuilder.empty().create())).isEmpty();
		assertThat(render("context: pi", LogEntryBuilder.empty().context("pi", "3.14").create())).isEqualTo("3.14");
	}

	/**
	 * Verifies that <tt>{context}</tt> without a defined key will produce an error.
	 */
	@Test
	public void contextMissingKey() {
		assertThat(render("context", LogEntryBuilder.empty().create())).isEmpty();
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("context");
	}

	/**
	 * Verifies that <tt>{context}</tt> can be parsed with a default value for non-existent mappings and the returned
	 * token will output the defined thread context value.
	 */

	@Test
	public void contextDefault() {
		assertThat(render("context: pi, -", LogEntryBuilder.empty().create())).isEqualTo("-");
		assertThat(render("context: pi, -", LogEntryBuilder.empty().context("pi", "3.14").create())).isEqualTo("3.14");
	}

	/**
	 * Verifies that <tt>{context}</tt> with a default value for non-existent mappings, but without a defined key will
	 * produce an error.
	 */
	@Test
	public void contextDefaultMissingKey() {
		assertThat(render("context: ,-", LogEntryBuilder.empty().create())).isEmpty();
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("context");
	}

	/**
	 * Verifies that <tt>{class}</tt> can be parsed and the returned token will output the fully-qualified class name.
	 */
	@Test
	public void fullClassName() {
		assertThat(render("class", LogEntryBuilder.empty().className("my.package.MyClass").create())).isEqualTo("my.package.MyClass");
	}

	/**
	 * Verifies that <tt>{className}</tt> can be parsed and the returned token will output the class name without
	 * package.
	 */
	@Test
	public void simpleClassName() {
		assertThat(render("className", LogEntryBuilder.empty().className("my.package.MyClass").create())).isEqualTo("MyClass");
	}

	/**
	 * Verifies that <tt>{package}</tt> can be parsed and the returned token will output the package name.
	 */
	@Test
	public void packageName() {
		assertThat(render("package", LogEntryBuilder.empty().className("my.package.MyClass").create())).isEqualTo("my.package");
	}

	/**
	 * Verifies that <tt>{method}</tt> can be parsed and the returned token will output the method name.
	 */
	@Test
	public void methodName() {
		assertThat(render("method", LogEntryBuilder.empty().methodName("foo").create())).isEqualTo("foo");
	}

	/**
	 * Verifies that <tt>{file}</tt> can be parsed and the returned token will output the file name.
	 */
	@Test
	public void fileName() {
		assertThat(render("file", LogEntryBuilder.empty().fileName("MyFile.java").create())).isEqualTo("MyFile.java");
	}

	/**
	 * Verifies that <tt>{line}</tt> can be parsed and the returned token will output the source line number.
	 */
	@Test
	public void lineNumber() {
		assertThat(render("line", LogEntryBuilder.empty().lineNumber(42).create())).isEqualTo("42");
	}

	/**
	 * Verifies that <tt>{tag}</tt> can be parsed and the returned token will output the logger tag if existing.
	 */
	@Test
	public void tag() {
		assertThat(render("tag", LogEntryBuilder.empty().tag("SYSTEM").create())).isEqualTo("SYSTEM");
		assertThat(render("tag", LogEntryBuilder.empty().create())).isEmpty();
	}

	/**
	 * Verifies that <tt>{tag}</tt> can be parsed with a default value for non-existent tags and the returned token will
	 * output the logger tag if existing.
	 */
	@Test
	public void tagDefault() {
		assertThat(render("tag", LogEntryBuilder.empty().tag("SYSTEM").create())).isEqualTo("SYSTEM");
		assertThat(render("tag: -", LogEntryBuilder.empty().create())).isEqualTo("-");
	}

	/**
	 * Verifies that <tt>{level}</tt> can be parsed and the returned token will output the severity level.
	 */
	@Test
	public void level() {
		assertThat(render("level", LogEntryBuilder.empty().level(Level.DEBUG).create())).isEqualTo("DEBUG");
	}

	/**
	 * Verifies that <tt>{message}</tt> can be parsed and the returned token will output the text message as well as the
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
	 * Verifies that <tt>{messageOnly}</tt> can be parsed and the returned token will output the text message, but not
	 * the exception.
	 */
	@Test
	public void messageOnly() {
		Exception exception = new NullPointerException();
		assertThat(render("messageOnly", LogEntryBuilder.empty().message("Hello World!").exception(exception).create()))
			.isEqualTo("Hello World!");
	}

	/**
	 * Verifies that <tt>{exception}</tt> can be parsed and the returned token will output the exception.
	 */
	@Test
	public void exception() {
		Exception exception = new NullPointerException();
		assertThat(render("exception", LogEntryBuilder.empty().exception(exception).create()))
			.contains(NullPointerException.class.getName())
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that <tt>{any | min-size=X}</tt> can be parsed and the returned token will apply minimum size.
	 */
	@Test
	public void minimumSize() {
		assertThat(render("{level | min-size=6}", LogEntryBuilder.empty().level(Level.INFO).create())).isEqualTo("INFO  ");
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
	 * Verifies that <tt>{any | indent=X}</tt> can be parsed and the returned token will apply indentation.
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
	 * Verifies that special characters can be escaped.
	 */
	@Test
	public void escaped() {
		assertThat(render("\\\\", LogEntryBuilder.empty().create())).isEqualTo("\\");
		assertThat(render("\\ ", LogEntryBuilder.empty().create())).isEqualTo(" ");
		assertThat(render("\\{\\}", LogEntryBuilder.empty().create())).isEqualTo("{}");
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
