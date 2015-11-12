/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.sameContent;
import static org.pmw.tinylog.hamcrest.StringMatchers.containsPattern;
import static org.pmw.tinylog.hamcrest.StringMatchers.hasLength;
import static org.pmw.tinylog.hamcrest.StringMatchers.matchesPattern;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.writers.LogEntryValue;

import mockit.Expectations;
import mockit.Verifications;

/**
 * Tests for tokenizer.
 *
 * @see Tokenizer
 */
public class TokenizerTest extends AbstractTest {

	private Locale locale;

	/**
	 * Initiate locale.
	 */
	@Before
	public final void init() {
		locale = Locale.ROOT;
	}

	/**
	 * Test parsing empty format patterns.
	 */
	@Test
	public final void testEmptyFormatPatterns() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("");
		assertThat(tokens, empty());

		tokens = tokenizer.parse("{}");
		assertThat(tokens, empty());
	}

	/**
	 * Test parsing with results of plain text tokens.
	 */
	@Test
	public final void testPlainTextToken() {
		List<Token> tokens = new Tokenizer(locale, 0).parse("Hello!");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals("Hello!", render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test parsing with results of date tokens.
	 */
	@Test
	public final void testDateToken() {
		Date date = new Date();
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{date}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.DATE));
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), render(tokens, new LogEntryBuilder().date(date)));

		tokens = tokenizer.parse("{date:yyyy}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.DATE));
		assertEquals(new SimpleDateFormat("yyyy").format(date), render(tokens, new LogEntryBuilder().date(date)));

		tokens = tokenizer.parse("{date:'}");
		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER ERROR\\: \"\\'\" is an invalid date format pattern \\(.+\\)"));
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.DATE));
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), render(tokens, new LogEntryBuilder().date(date)));
	}

	/**
	 * Test caching of rendered dates.
	 */
	@Test
	public final void testDateCaching() {
		List<Token> tokens = new Tokenizer(locale, 0).parse("{date: HH:mm:ss.SSS}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.DATE));

		final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

		Date date = new Date();
		LogEntryBuilder logEntryBuilder = new LogEntryBuilder().date(date);
		String expected = formatter.format(date);

		new Expectations(SimpleDateFormat.class) {
		};

		assertEquals(expected, render(tokens, logEntryBuilder));
		assertEquals(expected, render(tokens, logEntryBuilder));

		new Verifications(1) {
			{
				formatter.format((Date) any, (StringBuffer) any, (FieldPosition) any);
			}
		};

		date = new Date(date.getTime() + 1);
		logEntryBuilder = new LogEntryBuilder().date(date);
		expected = formatter.format(date);

		assertEquals(expected, render(tokens, logEntryBuilder));
	}

	/**
	 * Test parsing with results of process ID tokens (pid).
	 */
	@Test
	public final void testProcessIdToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{pid}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(EnvironmentHelper.getProcessId().toString(), render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("{pid:abc}");
		assertEquals("LOGGER WARNING: \"{pid}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(EnvironmentHelper.getProcessId().toString(), render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test parsing with results of thread name tokens.
	 */
	@Test
	public final void testThreadNameToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{thread}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.THREAD));
		assertEquals("test", render(tokens, new LogEntryBuilder().thread(new Thread("test"))));

		tokens = tokenizer.parse("{thread:abc}");
		assertEquals("LOGGER WARNING: \"{thread}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.THREAD));
		assertEquals("test", render(tokens, new LogEntryBuilder().thread(new Thread("test"))));
	}

	/**
	 * Test parsing with results of thread ID tokens.
	 */
	@Test
	public final void testThreadIdToken() {
		Thread thread = new Thread();
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{thread_id}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.THREAD));
		assertEquals(Long.toString(thread.getId()), render(tokens, new LogEntryBuilder().thread(thread)));

		tokenizer.parse("{thread_id:abc}");
		assertEquals("LOGGER WARNING: \"{thread_id}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.THREAD));
		assertEquals(Long.toString(thread.getId()), render(tokens, new LogEntryBuilder().thread(thread)));
	}

	/**
	 * Test parsing with results of class tokens.
	 */
	@Test
	public final void testClassToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{class}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("my.package.MyClass", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));

		tokens = tokenizer.parse("{class:abc}");
		assertEquals("LOGGER WARNING: \"{class}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("my.package.MyClass", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test parsing with results of class name tokens.
	 */
	@Test
	public final void testClassNameToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{class_name}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));

		tokenizer.parse("{class_name:abc}");
		assertEquals("LOGGER WARNING: \"{class_name}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test parsing with results of package tokens.
	 */
	@Test
	public final void testPackageToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{package}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("my.package", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("", render(tokens, new LogEntryBuilder().className("MyClass")));

		tokenizer.parse("{package:abc}");
		assertEquals("LOGGER WARNING: \"{package}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("my.package", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test parsing with results of method tokens.
	 */
	@Test
	public final void testMethodToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{method}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.METHOD));
		assertEquals("MyMethod", render(tokens, new LogEntryBuilder().method("MyMethod")));

		tokens = tokenizer.parse("{method:abc}");
		assertEquals("LOGGER WARNING: \"{method}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.METHOD));
		assertEquals("MyMethod", render(tokens, new LogEntryBuilder().method("MyMethod")));
	}

	/**
	 * Test parsing with results of file tokens.
	 */
	@Test
	public final void testFileToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{file}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.FILE));
		assertEquals("MyFile", render(tokens, new LogEntryBuilder().file("MyFile")));

		tokens = tokenizer.parse("{file:abc}");
		assertEquals("LOGGER WARNING: \"{file}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.FILE));
		assertEquals("MyFile", render(tokens, new LogEntryBuilder().file("MyFile")));
	}

	/**
	 * Test parsing with results of line tokens.
	 */
	@Test
	public final void testLineToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{line}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LINE));
		assertEquals("42", render(tokens, new LogEntryBuilder().lineNumber(42)));

		tokens = tokenizer.parse("{line:abc}");
		assertEquals("LOGGER WARNING: \"{line}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LINE));
		assertEquals("42", render(tokens, new LogEntryBuilder().lineNumber(42)));
	}

	/**
	 * Test parsing with results of level tokens.
	 */
	@Test
	public final void testLevelToken() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{level}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("DEBUG", render(tokens, new LogEntryBuilder().level(Level.DEBUG)));

		tokens = tokenizer.parse("{level:abc}");
		assertEquals("LOGGER WARNING: \"{level}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("DEBUG", render(tokens, new LogEntryBuilder().level(Level.DEBUG)));
	}

	/**
	 * Test parsing with results of message tokens.
	 */
	@Test
	public final void testMessageToken() {
		String newLine = EnvironmentHelper.getNewLine();

		/* No stack trace */

		List<Token> tokens = new Tokenizer(locale, 0).parse("{message}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));

		tokens = new Tokenizer(locale, 0).parse("{message:abc}");
		assertEquals("LOGGER WARNING: \"{message}\" does not support parameters", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));

		assertEquals("", render(tokens, new LogEntryBuilder().message(null)));
		assertEquals("Hello world", render(tokens, new LogEntryBuilder().message("Hello world")));
		assertEquals("java.lang.Throwable", render(tokens, new LogEntryBuilder().message(null).exception(new Throwable())));
		assertEquals("Hello: java.lang.Throwable", render(tokens, new LogEntryBuilder().message("Hello").exception(new Throwable())));
		assertEquals("java.lang.Throwable: Hello", render(tokens, new LogEntryBuilder().message(null).exception(new Throwable("Hello"))));

		/* One line stack trace */

		tokens = new Tokenizer(locale, 1).parse("{message}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));

		String renderedLogEntry = render(tokens, new LogEntryBuilder().message(null).exception(new Exception("Test")));
		assertThat(renderedLogEntry, matchesPattern("java\\.lang\\.Exception\\: Test" + newLine
				+ "\tat org.pmw.tinylog.TokenizerTest.testMessageToken\\(TokenizerTest.java:\\d*\\)" + newLine + "\t\\.\\.\\."));

		/* Full stack trace */

		tokens = new Tokenizer(locale, Integer.MAX_VALUE).parse("{message}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));

		renderedLogEntry = render(tokens, new LogEntryBuilder().message(null).exception(new RuntimeException(new NullPointerException())));
		assertThat(renderedLogEntry, containsPattern("java\\.lang\\.RuntimeException.*" + newLine
				+ "\tat org.pmw.tinylog.TokenizerTest.testMessageToken\\(TokenizerTest.java:\\d*\\)" + newLine));
		assertThat(renderedLogEntry, containsPattern("Caused by: java\\.lang\\.NullPointerException" + newLine
				+ "\tat org.pmw.tinylog.TokenizerTest.testMessageToken\\(TokenizerTest.java:\\d*\\)" + newLine));
	}

	/**
	 * Test parsing a format pattern with a result of multiple tokens.
	 */
	@Test
	public final void testMultiTokens() {
		List<Token> tokens = new Tokenizer(locale, 0).parse("Hello {thread}!\nI'm {method}");
		assertEquals("Hello #THREAD#!" + EnvironmentHelper.getNewLine() + "I'm #METHOD#",
				render(tokens, new LogEntryBuilder().thread(new Thread("#THREAD#")).method("#METHOD#")));
	}

	/**
	 * Test tokens that are nested.
	 */
	@Test
	public final void testNestedTokens() {
		String pid = EnvironmentHelper.getProcessId().toString();
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{{pid}}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(pid, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("{Hello from process {pid}!}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals("Hello from process " + pid + "!", render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("{pid{level}}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("pidINFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));

		tokens = tokenizer.parse("Hello from {{class_name} in {thread} of process {pid}}!");
		assertEquals(3, tokens.size());
		assertThat(tokens.get(1).getRequiredLogEntryValues(), containsInAnyOrder(LogEntryValue.CLASS, LogEntryValue.THREAD));
		assertEquals("Hello from MyClass in Thread ONE of process " + pid + "!",
				render(tokens, new LogEntryBuilder().className("my.package.MyClass").thread(new Thread("Thread ONE"))));
	}

	/**
	 * Test options for tokens.
	 */
	@Test
	public final void testOptions() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{class|option}");
		assertEquals("LOGGER WARNING: Unknown option \"option\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));

		tokens = tokenizer.parse("{class|key=value}");
		assertEquals("LOGGER WARNING: Unknown option \"key\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));

		tokens = tokenizer.parse("{class|option,min-size=10,key=value}");
		assertEquals("LOGGER WARNING: Unknown option \"option\"", getErrorStream().nextLine());
		assertEquals("LOGGER WARNING: Unknown option \"key\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.CLASS));
		assertEquals("MyClass   ", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test minimum size for tokens.
	 */
	@Test
	public final void testMinSize() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		/* Valid definitions of minimum sizes */

		List<Token> tokens = tokenizer.parse("{level|min-size=0}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{level|min-size=6}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO  ", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{level| min-size = 6 }");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO  ", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{{level}|min-size=7}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO   ", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{{level}:|min-size=8}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO:   ", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING:", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{{level}:|min-size=10,}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO:     ", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING:  ", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		/* Invalid definitions of minimum sizes */

		tokens = tokenizer.parse("{level|min-size}");
		assertEquals("LOGGER WARNING: No value set for \"min-size\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{level|min-size=-1}");
		assertEquals("LOGGER WARNING: \"-1\" is an invalid number for \"min-size\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{level|min-size=abc}");
		assertEquals("LOGGER WARNING: \"abc\" is an invalid number for \"min-size\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));

		tokens = tokenizer.parse("{level|min-size=}");
		assertEquals("LOGGER WARNING: No value set for \"min-size\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));
		assertEquals("WARNING", render(tokens, new LogEntryBuilder().level(Level.WARNING)));
	}

	/**
	 * Test indenting for tokens.
	 */
	@Test
	public final void testIndent() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		/* Valid definitions of indenting */

		List<Token> tokens = tokenizer.parse("{message|indent=0}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("TEST", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals("Hello\nWorld", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("{message|indent=1}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals(" TEST", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals(" Hello\r World", render(tokens, new LogEntryBuilder().message("Hello\rWorld")));

		tokens = tokenizer.parse("{message|indent=1}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals(" TEST", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals(" Hello\r\n World", render(tokens, new LogEntryBuilder().message("Hello\r\nWorld")));

		tokens = tokenizer.parse("{message|indent=1}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals(" TEST", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals(" Hello\n World", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("{message| indent = 1 }");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals(" TEST", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals(" Hello\n World", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("{{message}|indent=2}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("  TEST", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals("  Hello\n  World", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("{{message}!|indent=2}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("  TEST!", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals("  Hello\n  World!", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("{{message}!|indent=3,}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("   TEST!", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals("   Hello\n   World!", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("{!{message}!|indent=3}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("   !TEST!", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals("   !Hello\n   World!", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		tokens = tokenizer.parse("!{message|indent=3}!");
		assertEquals(3, tokens.size());
		assertThat(tokens.get(1).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("!TEST!", render(tokens, new LogEntryBuilder().message("TEST")));
		assertEquals("!Hello\n   World!", render(tokens, new LogEntryBuilder().message("Hello\nWorld")));

		/* Test removing whitespace */

		tokens = tokenizer.parse("{message|indent=2}");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("  TEST ", render(tokens, new LogEntryBuilder().message(" TEST ")));
		assertEquals("  Hello\n  World\t", render(tokens, new LogEntryBuilder().message("\tHello\n\tWorld\t")));

		/* Invalid definitions of indenting */

		tokens = tokenizer.parse("{message|indent}");
		assertEquals("LOGGER WARNING: No value set for \"indent\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("TEST", render(tokens, new LogEntryBuilder().message("TEST")));

		tokens = tokenizer.parse("{message|indent=-1}");
		assertEquals("LOGGER WARNING: \"-1\" is an invalid number for \"indent\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("TEST", render(tokens, new LogEntryBuilder().message("TEST")));

		tokens = tokenizer.parse("{message|indent=abc}");
		assertEquals("LOGGER WARNING: \"abc\" is an invalid number for \"indent\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("TEST", render(tokens, new LogEntryBuilder().message("TEST")));

		tokens = tokenizer.parse("{message|indent=}");
		assertEquals("LOGGER WARNING: No value set for \"indent\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.MESSAGE));
		assertEquals("TEST", render(tokens, new LogEntryBuilder().message("TEST")));
	}

	/**
	 * Test converting new lines.
	 */
	@Test
	public final void testNewLines() {
		String newLine = EnvironmentHelper.getNewLine();
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("\n");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\r");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\r\n");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\n\r");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine + newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\\n");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\\r");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\\r\\n");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\\n\\r");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(newLine + newLine, render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test converting tabs.
	 */
	@Test
	public final void testTabs() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("\t");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals("\t", render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("\\t");
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals("\t", render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test tokens that are invalid.
	 */
	@Test
	public final void testInvalidTokens() {
		Tokenizer tokenizer = new Tokenizer(locale, 0);

		List<Token> tokens = tokenizer.parse("{");
		assertEquals("LOGGER WARNING: Closing curly brace is missing for: \"{\"", getErrorStream().nextLine());
		assertThat(tokens, empty());

		tokens = tokenizer.parse("{pid");
		assertEquals("LOGGER WARNING: Closing curly brace is missing for: \"{pid\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals(EnvironmentHelper.getProcessId().toString(), render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("{pid|min-size=10");
		assertEquals("LOGGER WARNING: Closing curly brace is missing for: \"{pid|min-size=10\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertThat(render(tokens, new LogEntryBuilder()), allOf(startsWith(EnvironmentHelper.getProcessId().toString()), hasLength(10)));

		tokens = tokenizer.parse("{pid {level}");
		assertEquals("LOGGER WARNING: Closing curly brace is missing for: \"{pid {level}\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("pid INFO", render(tokens, new LogEntryBuilder().level(Level.INFO)));

		tokens = tokenizer.parse("}");
		assertEquals("LOGGER WARNING: Opening curly brace is missing for: \"}\"", getErrorStream().nextLine());
		assertThat(tokens, empty());

		tokens = tokenizer.parse("pid}");
		assertEquals("LOGGER WARNING: Opening curly brace is missing for: \"pid}\"", getErrorStream().nextLine());
		assertEquals(1, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), empty());
		assertEquals("pid", render(tokens, new LogEntryBuilder()));

		tokens = tokenizer.parse("{level} pid}");
		assertEquals("LOGGER WARNING: Opening curly brace is missing for: \"{level} pid}\"", getErrorStream().nextLine());
		assertEquals(2, tokens.size());
		assertThat(tokens.get(0).getRequiredLogEntryValues(), sameContent(LogEntryValue.LEVEL));
		assertEquals("INFO pid", render(tokens, new LogEntryBuilder().level(Level.INFO)));
	}

	private static String render(final List<Token> tokens, final LogEntryBuilder logEntryBuilder) {
		StringBuilder stringBuilder = new StringBuilder();
		LogEntry logEntry = logEntryBuilder.create();
		for (Token token : tokens) {
			token.render(logEntry, stringBuilder);
		}
		return stringBuilder.toString();
	}

}
