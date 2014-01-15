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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

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
	 * Test parsing with results of one single token.
	 */
	@Test
	public final void testSingleTokens() {
		List<Token> tokens = Tokenizer.parse("Hello!", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("Hello!", tokens.get(0).getData());

		tokens = Tokenizer.parse("{date", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("{date", tokens.get(0).getData());

		tokens = Tokenizer.parse("{{date}}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("{{date}}", tokens.get(0).getData());

		tokens = Tokenizer.parse("{date}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.DATE, tokens.get(0).getType());
		assertNotNull(tokens.get(0).getData());

		tokens = Tokenizer.parse("{date:yyyy}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.DATE, tokens.get(0).getType());
		assertEquals("yyyy", ((SimpleDateFormat) tokens.get(0).getData()).toPattern());

		tokens = Tokenizer.parse("{pid}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(EnvironmentHelper.getProcessId(), tokens.get(0).getData());

		tokens = Tokenizer.parse("{thread}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.THREAD, tokens.get(0).getType());

		tokens = Tokenizer.parse("{thread_id}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.THREAD_ID, tokens.get(0).getType());

		tokens = Tokenizer.parse("{class}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.CLASS, tokens.get(0).getType());

		tokens = Tokenizer.parse("{package}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PACKAGE, tokens.get(0).getType());

		tokens = Tokenizer.parse("{class_name}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.CLASS_NAME, tokens.get(0).getType());

		tokens = Tokenizer.parse("{method}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.METHOD, tokens.get(0).getType());

		tokens = Tokenizer.parse("{file}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.FILE, tokens.get(0).getType());

		tokens = Tokenizer.parse("{line}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.LINE_NUMBER, tokens.get(0).getType());

		tokens = Tokenizer.parse("{level}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.LOGGING_LEVEL, tokens.get(0).getType());

		tokens = Tokenizer.parse("{message}", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.MESSAGE, tokens.get(0).getType());
	}

	/**
	 * Test parsing a format pattern with a result of multiple tokens.
	 */
	@Test
	public final void testMultiTokens() {
		List<Token> tokens = Tokenizer.parse("Hello {thread}!\nI'm {method} and this {method is invalid", locale);
		assertEquals(6, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("Hello ", tokens.get(0).getData());
		assertEquals(TokenType.THREAD, tokens.get(1).getType());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(2).getType());
		assertEquals("!" + System.getProperty("line.separator") + "I'm ", tokens.get(2).getData());
		assertEquals(TokenType.METHOD, tokens.get(3).getType());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(4).getType());
		assertEquals(" and this ", tokens.get(4).getData());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(5).getType());
		assertEquals("{method is invalid", tokens.get(5).getData());
	}

	/**
	 * Test converting new lines.
	 */
	@Test
	public final void testNewLines() {
		String newLine = System.getProperty("line.separator");

		List<Token> tokens = Tokenizer.parse("\n", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\r", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\r\n", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\n\r", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine + newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\n", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\r", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\r\\n", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\n\\r", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine + newLine, tokens.get(0).getData());
	}

	/**
	 * Test converting tabs.
	 */
	@Test
	public final void testTabs() {
		List<Token> tokens = Tokenizer.parse("\t", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("\t", tokens.get(0).getData());

		tokens = Tokenizer.parse("\\t", locale);
		assertEquals(1, tokens.size());
		assertEquals(TokenType.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("\t", tokens.get(0).getData());
	}

}
