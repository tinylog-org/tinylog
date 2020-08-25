/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.converters;

import org.tinylog.writers.RollingFileWriter;

/**
 * Log file converter for the {@link RollingFileWriter}.
 *
 * <p>
 *     All methods are called synchronously by tinylog and will block the program flow. Therefore, slow IO operations
 *     and long running computing algorithms should be run in separate non-blocking threads.
 * </p>
 */
public interface FileConverter {

	/**
	 * Gets the additional file extension for backup files.
	 *
	 * <p>
	 *     The method can return {@code null}, if this converter does not create backup files with different file
	 *     extensions.
	 * </p>
	 *
	 * @return Additional file extension or {@code null}
	 */
	String getBackupSuffix();

	/**
	 * This method is called when tinylog opens a log file for writing log entries.
	 *
	 * @param fileName
	 *            Log file
	 */
	void open(String fileName);

	/**
	 * This method can convert data before writing to the currently opened log file.
	 *
	 * @param data
	 *            Data to write to the currently opened log file
	 * @return The passed byte array or a new byte array that should be written instead
	 */
	byte[] write(byte[] data);

	/**
	 * This method is called when tinylog closes the current log file.
	 */
	void close();

	/**
	 * Shuts this file converter down.
	 *
	 * <p>
	 *     If the converter has started any further threads, this method must await their termination.
	 * </p>
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting
	 */
	void shutdown() throws InterruptedException;

}
