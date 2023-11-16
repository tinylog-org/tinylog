/*
 * Copyright 2023 Piotr Karlowicz
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

package org.tinylog.writers.raw;

import org.tinylog.Level;

/**
 * Syslog severity levels.
 */
public enum SyslogSeverity {
	
	/**
	 * System is unusable.
	 */
	EMERGENCY(0),
	/**
	 * Action must be taken immediately.
	 */
	ALERT(1),
	/**
	 * Critical conditions.
	 */
	CRITICAL(2),
	/**
	 * Error conditions.
	 */
	ERROR(3),
	/**
	 * Warning conditions.
	 */
	WARNING(4),
	/**
	 * Normal but significant conditions.
	 */
	NOTICE(5),
	/**
	 * Informational messages.
	 */
	INFORMATIONAL(6),
	/**
	 * Debug level messages.
	 */
	DEBUG(7);

	private final int code;

	SyslogSeverity(final int code) {
		this.code = code;
	}

	/**
	 * Returns the severity code.
	 * @return The numeric value associated with the Severity.
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Returns the Severity for the specified Level.
	 * @param level The Level.
	 * @return The matching Severity, or INFORMATIONAL if there is no match.
	 */
	public static SyslogSeverity getSeverity(final Level level) {
		switch (level) {
			case TRACE:
			case DEBUG:
				return DEBUG;
			case INFO:
				return INFORMATIONAL;
			case WARN:
				return WARNING;
			case ERROR:
				return ERROR;
			case OFF:
				return EMERGENCY;
			default:
				return INFORMATIONAL;
		}
	}
}
