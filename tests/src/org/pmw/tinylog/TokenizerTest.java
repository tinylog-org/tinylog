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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pmw.tinylog.hamcrest.RegexMatchers.contains;
import static org.pmw.tinylog.hamcrest.RegexMatchers.matches;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.util.LogEntryBuilder;

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
		locale = Locale.getDefault();
	}

	/**
	 * Test if the class is a valid utility class.
	 *
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(Tokenizer.class);
	}

	/**
	 * Test parsing with results of plain text tokens.
	 */
	@Test
	public final void testPlainTextToken() {
		List<Token> tokens = Tokenizer.parse("Hello!", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("Hello!", render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test parsing with results of date tokens.
	 */
	@Test
	public final void testDateToken() {
		Date date = new Date();

		List<Token> tokens = Tokenizer.parse("{date}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.DATE, tokens.get(0).getType());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), render(tokens, new LogEntryBuilder().date(date)));

		tokens = Tokenizer.parse("{date:yyyy}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.DATE, tokens.get(0).getType());
		assertEquals(new SimpleDateFormat("yyyy").format(date), render(tokens, new LogEntryBuilder().date(date)));

		tokens = Tokenizer.parse("{date:'}", locale, 0);
		assertThat(getErrorStream().nextLine(), containsString("invalid date format pattern"));
		assertEquals(1, tokens.size());
		assertEquals(TokenType.DATE, tokens.get(0).getType());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), render(tokens, new LogEntryBuilder().date(date)));
	}

	/**
	 * Test parsing with results of process ID tokens (pid).
	 */
	@Test
	public final void testProcessIdToken() {
		List<Token> tokens = Tokenizer.parse("{pid}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(EnvironmentHelper.getProcessId().toString(), render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test parsing with results of thread name tokens.
	 */
	@Test
	public final void testThreadNameToken() {
		List<Token> tokens = Tokenizer.parse("{thread}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.THREAD_NAME, tokens.get(0).getType());
		assertEquals("test", render(tokens, new LogEntryBuilder().thread(new Thread("test"))));
	}

	/**
	 * Test parsing with results of thread ID tokens.
	 */
	@Test
	public final void testThreadIdToken() {
		Thread thread = new Thread();

		List<Token> tokens = Tokenizer.parse("{thread_id}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.THREAD_ID, tokens.get(0).getType());
		assertEquals(Long.toString(thread.getId()), render(tokens, new LogEntryBuilder().thread(thread)));
	}

	/**
	 * Test parsing with results of class tokens.
	 */
	@Test
	public final void testClassToken() {
		List<Token> tokens = Tokenizer.parse("{class}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.CLASS, tokens.get(0).getType());
		assertEquals("my.package.MyClass", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test parsing with results of class name tokens.
	 */
	@Test
	public final void testClassNameToken() {
		List<Token> tokens = Tokenizer.parse("{class_name}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.CLASS_NAME, tokens.get(0).getType());
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("MyClass", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test parsing with results of package tokens.
	 */
	@Test
	public final void testPackageToken() {
		List<Token> tokens = Tokenizer.parse("{package}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PACKAGE, tokens.get(0).getType());
		assertEquals("my.package", render(tokens, new LogEntryBuilder().className("my.package.MyClass")));
		assertEquals("", render(tokens, new LogEntryBuilder().className("MyClass")));
	}

	/**
	 * Test parsing with results of method tokens.
	 */
	@Test
	public final void testMethodToken() {
		List<Token> tokens = Tokenizer.parse("{method}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.METHOD, tokens.get(0).getType());
		assertEquals("MyMethod", render(tokens, new LogEntryBuilder().method("MyMethod")));
	}

	/**
	 * Test parsing with results of file tokens.
	 */
	@Test
	public final void testFileToken() {
		List<Token> tokens = Tokenizer.parse("{file}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.FILE, tokens.get(0).getType());
		assertEquals("MyFile", render(tokens, new LogEntryBuilder().file("MyFile")));
	}

	/**
	 * Test parsing with results of line tokens.
	 */
	@Test
	public final void testLineToken() {
		List<Token> tokens = Tokenizer.parse("{line}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.LINE, tokens.get(0).getType());
		assertEquals("42", render(tokens, new LogEntryBuilder().lineNumber(42)));
	}

	/**
	 * Test parsing with results of level tokens.
	 */
	@Test
	public final void testLevelToken() {
		List<Token> tokens = Tokenizer.parse("{level}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.LEVEL, tokens.get(0).getType());
		assertEquals("DEBUG", render(tokens, new LogEntryBuilder().level(Level.DEBUG)));
	}

	/**
	 * Test parsing with results of message tokens.
	 */
	@Test
	public final void testMessageToken() {
		String newLine = EnvironmentHelper.getNewLine();

		/* No stack trace */

		List<Token> tokens = Tokenizer.parse("{message}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.MESSAGE, tokens.get(0).getType());

		assertEquals("", render(tokens, new LogEntryBuilder().message(null)));
		assertEquals("Hello world", render(tokens, new LogEntryBuilder().message("Hello world")));
		assertEquals("java.lang.Throwable", render(tokens, new LogEntryBuilder().message(null).exception(new Throwable())));
		assertEquals("Hello: java.lang.Throwable", render(tokens, new LogEntryBuilder().message("Hello").exception(new Throwable())));
		assertEquals("java.lang.Throwable: Hello", render(tokens, new LogEntryBuilder().message(null).exception(new Throwable("Hello"))));

		/* One line stack trace */

		tokens = Tokenizer.parse("{message}", locale, 1);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.MESSAGE, tokens.get(0).getType());

		String renderedLogEntry = render(tokens, new LogEntryBuilder().message(null).exception(new Exception("Test")));
		assertThat(renderedLogEntry, matches("java\\.lang\\.Exception\\: Test" + newLine
				+ "\tat org.pmw.tinylog.TokenizerTest.testMessageToken\\(TokenizerTest.java:\\d*\\)" + newLine + "\t\\.\\.\\."));

		/* Full stack trace */

		tokens = Tokenizer.parse("{message}", locale, Integer.MAX_VALUE);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.MESSAGE, tokens.get(0).getType());

		renderedLogEntry = render(tokens, new LogEntryBuilder().message(null).exception(new RuntimeException(new NullPointerException())));
		assertThat(renderedLogEntry, contains("java\\.lang\\.RuntimeException.*" + newLine
				+ "\tat org.pmw.tinylog.TokenizerTest.testMessageToken\\(TokenizerTest.java:\\d*\\)" + newLine));
		assertThat(renderedLogEntry, contains("Caused by: java\\.lang\\.NullPointerException" + newLine
				+ "\tat org.pmw.tinylog.TokenizerTest.testMessageToken\\(TokenizerTest.java:\\d*\\)" + newLine));
	}

	/**
	 * Test parsing a format pattern with a result of multiple tokens.
	 */
	@Test
	public final void testMultiTokens() {
		List<Token> tokens = Tokenizer.parse("Hello {thread}!\nI'm {method} and this {method is invalid", locale, 0);
		assertEquals("Hello #THREAD#!" + EnvironmentHelper.getNewLine() + "I'm #METHOD# and this {method is invalid",
				render(tokens, new LogEntryBuilder().thread(new Thread("#THREAD#")).method("#METHOD#")));
	}

	/**
	 * Test converting new lines.
	 */
	@Test
	public final void testNewLines() {
		String newLine = EnvironmentHelper.getNewLine();

		List<Token> tokens = Tokenizer.parse("\n", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\r", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\r\n", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\n\r", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine + newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\\n", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\\r", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\\r\\n", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\\n\\r", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine + newLine, render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test converting tabs.
	 */
	@Test
	public final void testTabs() {
		List<Token> tokens = Tokenizer.parse("\t", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("\t", render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("\\t", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("\t", render(tokens, new LogEntryBuilder()));
	}

	/**
	 * Test tokens that are invalid tested.
	 */
	@Test
	public final void testInvalidNestedTokens() {
		List<Token> tokens = Tokenizer.parse("{date", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("{date", render(tokens, new LogEntryBuilder()));

		tokens = Tokenizer.parse("{{date}}", locale, 0);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("{{date}}", render(tokens, new LogEntryBuilder()));
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
