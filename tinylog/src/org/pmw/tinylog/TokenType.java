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
 * Token types.
 */
enum TokenType {

	/**
	 * Plain text
	 */
	PLAIN_TEXT,

	/**
	 * The current date
	 */
	DATE,

	/**
	 * The name of the current thread
	 */
	THREAD,

	/**
	 * The fully qualified class name of the caller
	 */
	CLASS,

	/**
	 * The class name of the caller
	 */
	CLASS_NAME,

	/**
	 * The package name of the caller
	 */
	PACKAGE,

	/**
	 * The method name of the caller
	 */
	METHOD,

	/**
	 * The source filename of the caller
	 */
	FILE,

	/**
	 * The line number of calling
	 */
	LINE_NUMBER,

	/**
	 * The logging level
	 * 
	 * @see LoggingLevel
	 */
	LOGGING_LEVEL,

	/**
	 * The logging message (including a possible exception stack trace)
	 */
	MESSAGE;

}
