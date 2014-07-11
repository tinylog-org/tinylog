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
 * The format pattern for log entries will be split in tokens.
 *
 * @see Logger#setLoggingFormat(String)
 */
interface Token {

	/**
	 * Returns the token type.
	 *
	 * @return Token type
	 */
	TokenType getType();

	/**
	 * Render the token.
	 *
	 * @param logEntry
	 *            LogEntry information
	 * @param builder
	 *            Builder to add rendered token
	 */
	void render(LogEntry logEntry, StringBuilder builder);

}
