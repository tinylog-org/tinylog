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

/**
 * The logging format pattern will be split in tokens.
 * 
 * @see Logger#setLoggingFormat(String)
 */
final class Token {

	private final EToken type;
	private final Object data;

	/**
	 * @param type
	 *            Token type
	 */
	public Token(final EToken type) {
		this.type = type;
		this.data = null;
	}

	/**
	 * 
	 * @param type
	 *            Token type
	 * @param data
	 *            Additional data for token (can be <code>null</code>)
	 */
	public Token(final EToken type, final Object data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Get the token type.
	 * 
	 * @return Token type
	 */
	public EToken getType() {
		return type;
	}

	/**
	 * Get the additional data of the token.
	 * 
	 * @return Additional data (can be <code>null</code>)
	 */
	public Object getData() {
		return data;
	}

}
