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

import java.io.FileOutputStream;
import java.io.IOException;

import org.pmw.tinylog.LoggingLevel;

/**
 * Writes log entries to a file.
 */
@PropertiesSupport(name = "file", properties = @Property(name = "filename", type = String.class))
public final class FileWriter implements LoggingWriter {

	private final String filename;
	private FileOutputStream stream;

	/**
	 * @param filename
	 *            Filename of the log file
	 */
	public FileWriter(final String filename) {
		this.filename = filename;
	}

	/**
	 * Get the filename of the log file.
	 * 
	 * @return Filename of the log file
	 */
	public String getFilename() {
		return filename;
	}

	@Override
	public void init() throws IOException {
		stream = new FileOutputStream(filename);
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) throws IOException {
		stream.write(logEntry.getBytes());
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public void close() throws IOException {
		stream.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

}
