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

import java.io.File;
import java.io.IOException;

/**
 * Writes log entries to a file like {@link org.pmw.tinylog.FileLoggingWriter} but keep backups of old logging files.
 */
public class RollingFileLoggingWriter implements ILoggingWriter {

	private final File file;
	private final int maxBackups;
	private final int maxSize;
	private int size;
	private FileLoggingWriter writer;

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param maxBackups
	 *            Number of backups
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public RollingFileLoggingWriter(final String filename, final int maxBackups) throws IOException {
		this(filename, maxBackups, 0);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param maxBackups
	 *            Number of backups
	 * @param maxSize
	 *            Maximum number of characters to write in a log file ("0" for no limitation)
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public RollingFileLoggingWriter(final String filename, final int maxBackups, final int maxSize) throws IOException {
		this.file = new File(filename);
		this.maxBackups = Math.max(0, maxBackups);
		this.maxSize = maxSize;
		this.size = 0;
		roll();
		this.writer = new FileLoggingWriter(file.getPath());
	}

	/**
	 * Returns the supported properties for this writer.
	 * 
	 * The rolling file logging writer needs a "filename" and the number of backups ("maxBackups") plus optionally the
	 * limit for file size ("maxSize") for initiation.
	 * 
	 * @return Two string array with and without the property "maxSize"
	 */
	public static String[][] getSupportedProperties() {
		return new String[][] { new String[] { "filename", "maxBackups" }, new String[] { "filename", "maxBackups", "maxSize" } };
	}

	@Override
	public final void write(final ELoggingLevel level, final String logEntry) {
		if (maxSize <= 0) {
			writer.write(level, logEntry);
		} else {
			synchronized (this) {
				if (size + logEntry.getBytes().length > maxSize) {
					try {
						writer.close();
						roll();
						writer = new FileLoggingWriter(file.getPath());
						size = 0;
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
				size += logEntry.length();
				writer.write(level, logEntry);
			}
		}
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public final void close() throws IOException {
		synchronized (this) {
			writer.close();
		}
	}

	@Override
	protected final void finalize() throws Throwable {
		close();
	}

	private void roll() {
		if (file.exists()) {
			String filenameWithoutExtension;
			String filenameExtension;

			String path = file.getPath();
			String name = file.getName();
			int index = name.indexOf('.', 1);
			if (index > 0) {
				filenameWithoutExtension = path.substring(0, (path.length() - name.length()) + index);
				filenameExtension = name.substring(index);
			} else {
				filenameWithoutExtension = path;
				filenameExtension = "";
			}

			roll(file, 0, filenameWithoutExtension, filenameExtension);
		}
	}

	private void roll(final File baseFile, final int number, final String filenameWithoutExtension, final String filenameExtension) {
		File targetFile = new File(filenameWithoutExtension + "." + number + filenameExtension);
		if (targetFile.exists()) {
			roll(targetFile, number + 1, filenameWithoutExtension, filenameExtension);
		}
		if (number < maxBackups) {
			baseFile.renameTo(targetFile);
		} else {
			baseFile.delete();
		}
	}

}
