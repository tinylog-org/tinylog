/*
 * Copyright 2013 Martin Winandy
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for token.
 * 
 * @see Token
 */
public class TokenTest extends AbstractTest {

	/**
	 * Tests getters of token.
	 */
	@Test
	public final void testGetters() {
		Token token = new Token(TokenType.FILE);
		assertEquals(TokenType.FILE, token.getType());
		assertNull(token.getData());

		token = new Token(TokenType.MESSAGE, "My data");
		assertEquals(TokenType.MESSAGE, token.getType());
		assertEquals("My data", token.getData());
	}

	/**
	 * Tests equals() method of token.
	 */
	@Test
	public final void testEquals() {
		Token token = new Token(TokenType.FILE);
		assertTrue(token.equals(new Token(TokenType.FILE)));
		assertTrue(token.equals(new Token(TokenType.FILE, null)));
		assertFalse(token.equals(new Token(TokenType.FILE, "")));
		assertFalse(token.equals(new Token(TokenType.MESSAGE)));
		assertFalse(token.equals(TokenType.FILE));

		token = new Token(TokenType.MESSAGE, "My data");
		assertFalse(token.equals(new Token(TokenType.MESSAGE)));
		assertFalse(token.equals(new Token(TokenType.MESSAGE, null)));
		assertTrue(token.equals(new Token(TokenType.MESSAGE, "My data")));
		assertFalse(token.equals(new Token(TokenType.MESSAGE, "Other data")));
		assertFalse(token.equals(new Token(TokenType.FILE, "My data")));
		assertFalse(token.equals(TokenType.MESSAGE));
		assertFalse(token.equals("My data"));
	}

	/**
	 * Tests hasCode() method of token.
	 */
	@Test
	public final void testHashCode() {
		Token token = new Token(TokenType.FILE);
		assertEquals(new Token(TokenType.FILE).hashCode(), token.hashCode());
		assertEquals(new Token(TokenType.FILE, null).hashCode(), token.hashCode());

		token = new Token(TokenType.MESSAGE, "My data");
		assertEquals(new Token(TokenType.MESSAGE, "My data").hashCode(), token.hashCode());
	}

}
