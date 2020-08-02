/*
 * Copyright 2020 Martin Winandy
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
 * Severity levels for log entries and configuration.
 */
public enum Level {

	/**
	 * The off severity level is for configuration use only and disables any logging. Log entries must never have
	 * an off severity level assigned.
	 */
	OFF,

	/**
	 * Error log entries contain severe technical errors that prevent normal operation.
	 */
	ERROR,

	/**
	 * Warn log entries contain technical warnings that indicate that something has gone wrong, but do not prevent
	 * operation.
	 */
	WARN,

	/**
	 * Info log entries contain important and relevant information.
	 */
	INFO,

	/**
	 * Debug log entries contain detailed debug information for developers.
	 */
	DEBUG,

	/**
	 * Trace log entries contain very fine-grained debug information for developers, typically the flow through.
	 */
	TRACE

}
