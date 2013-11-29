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

import java.io.IOException;

import org.pmw.tinylog.LoggingLevel;

/**
 * Writes log entries to a file.
 */
public final class FileWriter implements LoggingWriter {

	private final String filename;
	private java.io.FileWriter writer;

	/**
	 * @param filename
	 *            Filename of the log file
	 */
	public FileWriter(final String filename) {
		this.filename = filename;
	}

	/**
	 * Returns the name of the writer.
	 * 
	 * @return "file"
	 */
	public static String getName() {
		return "file";
	}

	/**
	 * Returns the supported properties for this writer.
	 * 
	 * The file logging writer needs a "filename" for initiation.
	 * 
	 * @return String array with the single property "filename"
	 */
	public static String[][] getSupportedProperties() {
		return new String[][] { new String[] { "filename" } };
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
	public void init() {
		try {
			writer = new java.io.FileWriter(filename);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
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
	public void close() throws IOException {
		writer.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

}
