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

package org.tinylog.writers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.core.ConfigurationParser;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.provider.InternalLogger;

/**
 * Writer for outputting log entries to system output streams.
 *
 * <p>
 * The error output stream will be used for log entries with the severity level warning and error. The standard output
 * stream will used for all other log entries.
 * </p>
 */
public final class ConsoleWriter extends AbstractFormatPatternWriter {

	private final Level errorLevel;

	/** */
	public ConsoleWriter() {
		this(Collections.<String, String>emptyMap());
	}

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public ConsoleWriter(final Map<String, String> properties) {
		super(properties);
		
		// Set the default level for stderr logging
		Level levelStream = Level.WARN;
		
		// Check stream property
		String stream = getStringValue("stream");
		if (stream != null) {
			// Check whether we have the err@LEVEL syntax
			String[] streams = stream.split("@", 2);
			if (streams.length == 2) {
				levelStream = ConfigurationParser.parse(streams[1], levelStream);
				if (!streams[0].equals("err")) {
					InternalLogger.log(Level.ERROR, "Stream with level must be \"err\", \"" + streams[0] + "\" is an invalid name");
				}
				stream = null;
			}
		}
		
		if (stream == null) {
			errorLevel = levelStream;
		} else if ("err".equalsIgnoreCase(stream)) {
			errorLevel = Level.TRACE;
		} else if ("out".equalsIgnoreCase(stream)) {
			errorLevel = Level.OFF;
		} else {
			InternalLogger.log(Level.ERROR, "Stream must be \"out\" or \"err\", \"" + stream + "\" is an invalid stream name");
			errorLevel = levelStream;
		}
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> logEntryValues = super.getRequiredLogEntryValues();
		logEntryValues.add(LogEntryValue.LEVEL);
		return logEntryValues;
	}

	@Override
	public void write(final LogEntry logEntry) {
		if (logEntry.getLevel().ordinal() < errorLevel.ordinal()) {
			System.out.print(render(logEntry));
		} else {
			System.err.print(render(logEntry));
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

}
