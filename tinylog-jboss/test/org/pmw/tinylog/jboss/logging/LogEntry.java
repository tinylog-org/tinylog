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

package org.pmw.tinylog.jboss.logging;

import java.io.Serializable;
import java.util.Objects;

import org.jboss.logging.Logger.Level;

/**
 * Represents a log entry.
 */
public final class LogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Level level;
	private final Object message;
	private final Throwable exception;

	/**
	 * @param level
	 *            Severity level
	 * @param message
	 *            Logging message
	 */
	public LogEntry(final Level level, final Object message) {
		this.level = level;
		this.message = message;
		this.exception = null;
	}

	/**
	 * @param level
	 *            Severity level
	 * @param message
	 *            Logging message
	 * @param exception
	 *            Logged exception
	 */
	public LogEntry(final Level level, final Object message, final Throwable exception) {
		this.level = level;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Get the severity level.
	 * 
	 * @return Severity level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Get the logging message.
	 * 
	 * @return Logging message
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * Get the logged exception.
	 * 
	 * @return Logged exception
	 */
	public Throwable getException() {
		return exception;
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, message, exception);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof LogEntry) {
			LogEntry record = (LogEntry) obj;
			return Objects.equals(this.level, record.level) && Objects.equals(this.message, record.message) && Objects.equals(this.exception, record.exception);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(level).append(": ");
		if (message == null) {
			builder.append(exception);
		} else if (exception == null) {
			builder.append(message);
		} else {
			builder.append(message).append(" (").append(exception).append(")");
		}		
		return builder.toString();
	}

}
