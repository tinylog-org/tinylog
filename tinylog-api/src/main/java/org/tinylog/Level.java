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

package org.tinylog;

/**
 * Severity levels for log entries.
 */
public enum Level {

	/**
	 * Trace log entries contain very fine-grained debug information, typically the flow through.
	 */
	TRACE,

	/**
	 * Debug log entries contain common debug information.
	 */
	DEBUG,

	/**
	 * Info log entries contain important relevant information.
	 */
	INFO,

	/**
	 * Warn log entries contain technical warnings. Typically warnings do not prevent the application from continuing.
	 */
	WARN,

	/**
	 * Error log entries contain severe technical errors. Typically errors prevent a function from continuing normally.
	 */
	ERROR,

	/**
	 * Off severity level disables any logging.
	 */
	OFF;

}
