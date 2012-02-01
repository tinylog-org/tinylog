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

package org.pmw.tinylog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes log entries to a file.
 */
public class FileLoggingWriter implements ILoggingWriter {

	private final BufferedWriter writer;

	/**
	 * @param filename
	 *            Filename of the log file
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public FileLoggingWriter(final String filename) throws IOException {
		super();
		this.writer = new BufferedWriter(new FileWriter(filename));
	}

	@Override
	public final void write(final ELoggingLevel level, final String logEntry) {
		try {
			writer.write(logEntry);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public final void close() throws IOException {
		writer.close();
	}

	@Override
	protected final void finalize() throws Throwable {
		close();
	}
}
