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

package org.pmw.tinylog.util;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * A logging writer that just save the written log entry as string.
 */
public final class StoreWriter implements LoggingWriter {

	private LogEntry logEntry;

	@Override
	public void init() {
		// Do nothing
	}

	@Override
	public void write(final LoggingLevel level, final String text) {
		if (logEntry != null) {
			throw new RuntimeException("Previous message wasn't consumed");
		}
		logEntry = new LogEntry(level, text);
	}

	/**
	 * Get and remove the last written log entry.
	 * 
	 * @return Last written log entry
	 */
	public LogEntry consumeLogEntry() {
		LogEntry backup = this.logEntry;
		this.logEntry = null;
		return backup;
	}

	/**
	 * Represents a log entry.
	 */
	public static final class LogEntry {

		private final LoggingLevel level;
		private final String text;

		/**
		 * @param level
		 *            Level of the log entry
		 * @param text
		 *            Message of the log entry
		 */
		public LogEntry(final LoggingLevel level, final String text) {
			this.level = level;
			this.text = text;
		}

		/**
		 * Get the level of the log entry.
		 * 
		 * @return Level of the log entry
		 */
		public LoggingLevel getLevel() {
			return level;
		}

		/**
		 * Get the message of the log entry.
		 * 
		 * @return Message of the log entry
		 */
		public String getText() {
			return text;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			} else if (obj instanceof LogEntry) {
				LogEntry logEntry = (LogEntry) obj;
				if (level != logEntry.level) {
					return false;
				}
				if (text == null) {
					if (logEntry.text != null) {
						return false;
					}
				} else if (!text.equals(logEntry.text)) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return text.hashCode();
		}

		@Override
		public String toString() {
			return level + ": " + text;
		}

	}

}
