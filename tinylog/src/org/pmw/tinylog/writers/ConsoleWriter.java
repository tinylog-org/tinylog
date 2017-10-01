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

package org.pmw.tinylog.writers;

import java.io.PrintStream;
import java.util.EnumSet;
import java.util.Set;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntry;

/**
 * Writes log entries to the console.
 */
@PropertiesSupport(name = "console", properties = { @Property(name = "stream", type = String.class, optional = true) })
public final class ConsoleWriter implements Writer {

	private final PrintStream err;
	private final PrintStream out;

	public ConsoleWriter() {
		err = System.err;
		out = System.out;
	}

	public ConsoleWriter(final PrintStream stream) {
		err = stream;
		out = stream;
	}

	ConsoleWriter(final String stream) {
		if (stream == null) {
			err = System.err;
			out = System.out;
		} else if ("err".equalsIgnoreCase(stream)) {
			err = System.err;
			out = System.err;
		} else if ("out".equalsIgnoreCase(stream)) {
			err = System.out;
			out = System.out;
		} else {
			throw new IllegalArgumentException("Stream must be \"out\" or \"err\", \"" + stream + "\" is not a valid stream name");
		}
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY);
	}

	@Override
	public void init(final Configuration configuration) {
		// Do nothing
	}

	@Override
	public void write(final LogEntry logEntry) {
		getPrintStream(logEntry.getLevel()).print(logEntry.getRenderedLogEntry());
	}

	@Override
	public void flush() {
		// Do nothing
	}

	@Override
	public void close() {
		// Do nothing
	}

	private PrintStream getPrintStream(final Level level) {
		if (level == Level.ERROR || level == Level.WARNING) {
			return err;
		} else {
			return out;
		}
	}

}
