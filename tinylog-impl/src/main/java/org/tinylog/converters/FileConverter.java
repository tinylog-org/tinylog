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

import java.io.File;

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
	 *     The method can return {@code null} or an empty string, if this converter does not create backup files with
	 *     different file extension.
	 * </p>
	 *
	 * @return Additional file extension, {@code null}, or empty string
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
	 * <p>
	 *     If no converting is required, the original data can be passed as return value. If this method converts the
	 *     passed byte array, it has to return a new byte array. In this case, all bytes from the created byte array
	 *     will be written to the currently opened log file.
	 * </p>
	 *
	 * @param data
	 *            Data to write to the currently opened log file
	 * @param length
	 *            Number of bytes that are used to in the passed byte array (all remaining bytes have to be ignored)
	 * @return The passed byte array or a new byte array that should be written instead
	 */
	byte[] write(byte[] data, int length);

	/**
	 * This method is called when tinylog closes the current log file.
	 */
	void close();

}
