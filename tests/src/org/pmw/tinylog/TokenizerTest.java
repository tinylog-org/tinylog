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

import org.junit.Test;

/**
 * Tests for tokenizer.
 * 
 * @see Tokenizer
 */
public class TokenizerTest {

	/**
	 * Test parsing with results of one single token.
	 */
	@Test
	public final void testSingleTokens() {
		List<Token> tokens = Tokenizer.parse("Hello!");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("Hello!", tokens.get(0).getData());

		tokens = Tokenizer.parse("{date");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("{date", tokens.get(0).getData());

		tokens = Tokenizer.parse("{{date}}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("{{date}}", tokens.get(0).getData());

		tokens = Tokenizer.parse("{date}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.DATE, tokens.get(0).getType());
		assertNotNull(tokens.get(0).getData());

		tokens = Tokenizer.parse("{date:yyyy}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.DATE, tokens.get(0).getType());
		assertEquals(new SimpleDateFormat("yyyy"), tokens.get(0).getData());

		tokens = Tokenizer.parse("{thread}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.THREAD, tokens.get(0).getType());

		tokens = Tokenizer.parse("{method}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.METHOD, tokens.get(0).getType());

		tokens = Tokenizer.parse("{level}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.LOGGING_LEVEL, tokens.get(0).getType());

		tokens = Tokenizer.parse("{message}");
		assertEquals(1, tokens.size());
		assertEquals(EToken.MESSAGE, tokens.get(0).getType());
	}

	/**
	 * Test parsing a format pattern with a result of multiple tokens.
	 */
	@Test
	public final void testMultiTokens() {
		List<Token> tokens = Tokenizer.parse("Hello {thread}!\nI'm {method} and this {method is invalid");
		assertEquals(6, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals("Hello ", tokens.get(0).getData());
		assertEquals(EToken.THREAD, tokens.get(1).getType());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(2).getType());
		assertEquals("!" + System.getProperty("line.separator") + "I'm ", tokens.get(2).getData());
		assertEquals(EToken.METHOD, tokens.get(3).getType());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(4).getType());
		assertEquals(" and this ", tokens.get(4).getData());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(5).getType());
		assertEquals("{method is invalid", tokens.get(5).getData());
	}

	/**
	 * Test converting new lines.
	 */
	@Test
	public final void testNewLines() {
		String newLine = System.getProperty("line.separator");

		List<Token> tokens = Tokenizer.parse("\n");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\r");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\r\n");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\n\r");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine + newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\n");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\r");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\r\\n");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine, tokens.get(0).getData());

		tokens = Tokenizer.parse("\\n\\r");
		assertEquals(1, tokens.size());
		assertEquals(EToken.PLAIN_TEXT, tokens.get(0).getType());
		assertEquals(newLine + newLine, tokens.get(0).getData());
	}

}
