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

package org.tinylog.writers.raw;

import java.io.IOException;

/**
 * Writers output raw byte arrays.
 */
public interface ByteArrayWriter {

	/**
	 * Reads the last bytes.
	 *
	 * <p>
	 *     The bytes are read from the end of the current file. If the file size is equal to or greater than the passed
	 *     length, the passed array is filled completely. Otherwise, the passed array is filled with the entire
	 *     available file content and all remaining bytes of the array are left untouched.
	 * </p>
	 *
	 * @param data
	 *            Target byte array for storing the read bytes
	 * @param offset
	 *            Start offset to fill passed byte array
	 * @param length
	 *            Maximum number of bytes to read
	 * @return Number of read bytes
	 * @throws IOException
	 *             Reading failed
	 */
	int readTail(byte[] data, int offset, int length) throws IOException;

	/**
	 * Outputs a byte array.
	 *
	 * @param data
	 *            Byte array to output
	 * @param length
	 *            Number of bytes to output
	 * @throws IOException
	 *             Writing failed
	 * @deprecated Replaced by {@link #write(byte[], int, int)}
	 */
	@Deprecated
	void write(byte[] data, int length) throws IOException;

	/**
	 * Outputs a byte array.
	 *
	 * @param data
	 *            Byte array to output
	 * @param offset
	 *            Start offset to output
	 * @param length
	 *            Number of bytes to output
	 * @throws IOException
	 *             Writing failed
	 */
	void write(byte[] data, int offset, int length) throws IOException;

	/**
	 * Truncates the file size.
	 *
	 * @param count
	 *            Number of bytes to remove from the file end
	 * @throws IOException
	 *             Resizing failed
	 */
	void truncate(int count) throws IOException;

	/**
	 * Forces writing of any buffered data.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	void flush() throws IOException;

	/**
	 * Closes this writer and releases any associated system resources.
	 *
	 * @throws IOException
	 *             Closing failed
	 */
	void close() throws IOException;

}
