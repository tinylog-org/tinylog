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

import org.pmw.tinylog.writers.LogEntryValue;

/**
 * Token types.
 */
enum TokenType {

	/**
	 * Plain text
	 */
	PLAIN_TEXT(null),

	/**
	 * The current date
	 */
	DATE(LogEntryValue.DATE),

	/**
	 * The name of the current thread
	 */
	THREAD_NAME(LogEntryValue.THREAD),

	/**
	 * The ID of the current thread
	 */
	THREAD_ID(LogEntryValue.THREAD),

	/**
	 * The fully qualified class name of the caller
	 */
	CLASS(LogEntryValue.CLASS),

	/**
	 * The class name of the caller
	 */
	CLASS_NAME(LogEntryValue.CLASS),

	/**
	 * The package name of the caller
	 */
	PACKAGE(LogEntryValue.CLASS),

	/**
	 * The method name of the caller
	 */
	METHOD(LogEntryValue.METHOD),

	/**
	 * The source filename of the caller
	 */
	FILE(LogEntryValue.FILE),

	/**
	 * The line number of calling
	 */
	LINE_NUMBER(LogEntryValue.LINE_NUMBER),

	/**
	 * The severity level
	 * 
	 * @see Level
	 */
	LEVEL(LogEntryValue.LEVEL),

	/**
	 * The logging message (including a possible exception stack trace)
	 */
	MESSAGE(LogEntryValue.MESSAGE);

	private final LogEntryValue requiredLogEntryValue;

	private TokenType(final LogEntryValue requiredLogEntryValue) {
		this.requiredLogEntryValue = requiredLogEntryValue;
	}

	/**
	 * Get the required log entry value.
	 * 
	 * @return Required log entry value
	 */
	LogEntryValue getRequiredLogEntryValue() {
		return requiredLogEntryValue;
	}

}
