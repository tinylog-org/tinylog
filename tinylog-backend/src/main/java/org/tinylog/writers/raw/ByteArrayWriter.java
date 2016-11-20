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
	 * Outputs a byte array.
	 *
	 * @param data
	 *            Byte array to output
	 * @param length
	 *            Number of bytes to output
	 * @throws IOException
	 *             Writing failed
	 */
	void write(byte[] data, int length) throws IOException;

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
