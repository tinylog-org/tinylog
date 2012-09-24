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

package org.apache.log4j;

import org.pmw.tinylog.LogEntryForwarder;
import org.pmw.tinylog.LoggingLevel;

public abstract class Category {

	private static final Logger INSTANCE = Logger.getRootLogger();

	protected Category() {
	}

	@Deprecated
	final public static Category getRoot() {
		return INSTANCE;
	}

	@Deprecated
	public static Category getInstance(final Class<?> clazz) {
		return INSTANCE;
	}

	@Deprecated
	public static Category getInstance(final String name) {
		return INSTANCE;
	}

	@Deprecated
	public static Logger exists(final String name) {
		return INSTANCE;
	}

	public Level getLevel() {
		switch (org.pmw.tinylog.Logger.getLoggingLevel()) {
			case ERROR:
				return Level.ERROR;
			case WARNING:
				return Level.WARN;
			case INFO:
				return Level.INFO;
			case DEBUG:
				return Level.DEBUG;
			case TRACE:
				return Level.TRACE;
			case OFF:
				return Level.OFF;
			default:
				return null;
		}
	}

	public Level getEffectiveLevel() {
		return getLevel();
	}

	public void setLevel(final Level level) {
		// Ignore
	}

	@Deprecated
	public Level getPriority() {
		return getLevel();
	}

	public Priority getChainedPriority() {
		return getLevel();
	}

	@Deprecated
	public void setPriority(final Priority priority) {
		// Ignore
	}

	public void log(final Priority priority, final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, toLoggingLevel(priority), message);
		}
	}

	public void log(final Priority priority, final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, toLoggingLevel(priority), ex, null);
			} else {
				LogEntryForwarder.forward(1, toLoggingLevel(priority), ex, message.toString());
			}
		}
	}

	public void log(final String callerFQCN, final Priority level, final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, toLoggingLevel(level), ex, null);
			} else {
				LogEntryForwarder.forward(1, toLoggingLevel(level), ex, message.toString());
			}
		}
	}

	public void error(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.ERROR, message);
		}
	}

	public void error(final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, LoggingLevel.ERROR, ex, null);
			} else {
				LogEntryForwarder.forward(1, LoggingLevel.ERROR, ex, message.toString());
			}
		}
	}

	public void fatal(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.ERROR, message);
		}
	}

	public void fatal(final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, LoggingLevel.ERROR, ex, null);
			} else {
				LogEntryForwarder.forward(1, LoggingLevel.ERROR, ex, message.toString());
			}
		}
	}

	public void warn(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.WARNING, message);
		}
	}

	public void warn(final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, LoggingLevel.WARNING, ex, null);
			} else {
				LogEntryForwarder.forward(1, LoggingLevel.WARNING, ex, message.toString());
			}
		}
	}

	public void info(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.INFO, message);
		}
	}

	public void info(final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, LoggingLevel.INFO, ex, null);
			} else {
				LogEntryForwarder.forward(1, LoggingLevel.INFO, ex, message.toString());
			}
		}
	}

	public void debug(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.DEBUG, message);
		}
	}

	public void debug(final Object message, final Throwable ex) {
		if (message != null) {
			if (message == ex) {
				LogEntryForwarder.forward(1, LoggingLevel.DEBUG, ex, null);
			} else {
				LogEntryForwarder.forward(1, LoggingLevel.DEBUG, ex, message.toString());
			}
		}
	}

	public boolean isInfoEnabled() {
		return org.pmw.tinylog.Logger.getLoggingLevel().ordinal() <= LoggingLevel.INFO.ordinal();
	}

	public boolean isDebugEnabled() {
		return org.pmw.tinylog.Logger.getLoggingLevel().ordinal() <= LoggingLevel.DEBUG.ordinal();
	}

	public boolean isEnabledFor(final Priority level) {
		return level.isGreaterOrEqual(getLevel());
	}

	@SuppressWarnings("deprecation")
	private LoggingLevel toLoggingLevel(final Priority priority) {
		if (priority.isGreaterOrEqual(Priority.ERROR)) {
			return LoggingLevel.ERROR;
		} else if (priority.isGreaterOrEqual(Priority.WARN)) {
			return LoggingLevel.WARNING;
		} else if (priority.isGreaterOrEqual(Priority.INFO)) {
			return LoggingLevel.INFO;
		} else if (priority.isGreaterOrEqual(Priority.DEBUG)) {
			return LoggingLevel.DEBUG;
		} else {
			return LoggingLevel.TRACE;
		}
	}

}
