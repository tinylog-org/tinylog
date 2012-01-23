package org.pmw.tinylog.test;

import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.ILoggingWriter;

class LoggingWriter implements ILoggingWriter {

	private String entry;

	public LoggingWriter() {
	}

	@Override
	public final void write(final ELoggingLevel level, final String logEntry) {
		entry = logEntry;
	}

	public String consumeEntry() {
		String copy = entry;
		entry = null;
		return copy;
	}

}
