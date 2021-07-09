/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.tinylog.Level;

/**
 * This is intended to provide information about which log levels can be logged by a severity level. For example, TRACE is able to log all
 * levels, whereas ERROR can log only at the ERROR level and OFF means none of the levels may be logged.
 */
public final class LevelConfiguration {

	/**
	 * List of all severity levels each other levels are "enabled" by them. The other level is enabled if it can be logged. This usually
	 * means that the other level is also of a higher severity level.
	 */
	public static final Collection<LevelConfiguration> AVAILABLE_LEVELS = Collections.unmodifiableList(Arrays.asList(
		new LevelConfiguration(Level.TRACE, true, true, true, true, true),
		new LevelConfiguration(Level.DEBUG, false, true, true, true, true),
		new LevelConfiguration(Level.INFO, false, false, true, true, true),
		new LevelConfiguration(Level.WARN, false, false, false, true, true),
		new LevelConfiguration(Level.ERROR, false, false, false, false, true),
		new LevelConfiguration(Level.OFF, false, false, false, false, false)
	));

	private final Level level;
	private final boolean traceEnabled;
	private final boolean debugEnabled;
	private final boolean infoEnabled;
	private final boolean warnEnabled;
	private final boolean errorEnabled;

	/**
	 * @param level
	 *            Actual severity level
	 * @param traceEnabled
	 *            Determines if {@link Level#TRACE TRACE} level is enabled
	 * @param debugEnabled
	 *            Determines if {@link Level#DEBUG DEBUG} level is enabled
	 * @param infoEnabled
	 *            Determines if {@link Level#INFO INFO} level is enabled
	 * @param warnEnabled
	 *            Determines if {@link Level#WARN WARN} level is enabled
	 * @param errorEnabled
	 *            Determines if {@link Level#ERROR ERROR} level is enabled
	 */
	public LevelConfiguration(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
		final boolean warnEnabled, final boolean errorEnabled) {
		this.level = level;
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.warnEnabled = warnEnabled;
		this.errorEnabled = errorEnabled;
	}

	/** @return the actual severity level */
	public Level getLevel() {
		return level;
	}

	/** @return {@code true} if the TRACE level is enabled for the severity {@link #level}, otherwise {@code false} */
	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	/** @return {@code true} if the DEBUG level is enabled for the severity {@link #level}, otherwise {@code false} */
	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/** @return {@code true} if the INFO level is enabled for the severity {@link #level}, otherwise {@code false} */
	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	/** @return {@code true} if the WARN level is enabled for the severity {@link #level}, otherwise {@code false} */
	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	/** @return {@code true} if the ERROR level is enabled for the severity {@link #level}, otherwise {@code false} */
	public boolean isErrorEnabled() {
		return errorEnabled;
	}

	/**
	 * @return a textual representation of the severity level
	 */
	@Override
	public String toString() {
		return level.toString();
	}
}
