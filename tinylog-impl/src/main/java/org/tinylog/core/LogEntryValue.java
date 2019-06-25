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

package org.tinylog.core;

/**
 * Enumerated values for defining required log entry values.
 */
public enum LogEntryValue {

	/**
	 * Date and time of issue.
	 *
	 * @see LogEntry#getTimestamp()
	 */
	DATE,

	/**
	 * Issuing thread.
	 *
	 * @see LogEntry#getThread()
	 */
	THREAD,

	/**
	 * Thread context mapping as it was active while issuing.
	 *
	 * @see LogEntry#getContext()
	 */
	CONTEXT,

	/**
	 * Name of issuing class.
	 *
	 * @see LogEntry#getClassName()
	 */
	CLASS,

	/**
	 * Name of issuing method.
	 *
	 * @see LogEntry#getMethodName()
	 */
	METHOD,

	/**
	 * Name of issuing source file.
	 *
	 * @see LogEntry#getFileName()
	 */
	FILE,

	/**
	 * Issuing line number in source file.
	 *
	 * @see LogEntry#getLineNumber()
	 */
	LINE,

	/**
	 * Tag from tagged logger.
	 *
	 * @see LogEntry#getTag()
	 */
	TAG,

	/**
	 * Severity level.
	 *
	 * @see LogEntry#getLevel()
	 */
	LEVEL,

	/**
	 * Text message.
	 *
	 * @see LogEntry#getMessage()
	 */
	MESSAGE,

	/**
	 * Caught exception or throwable.
	 *
	 * @see LogEntry#getException()
	 */
	EXCEPTION;

}
