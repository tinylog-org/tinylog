/*
 * Copyright 2021 Martin Winandy
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
import java.util.Arrays;

/**
 * Wrapper for skipping charset headers (for example BOM for UTF-16).
 *
 * <p>
 *     Unfortunately, {@link String#getBytes()} can add a charset header (like a BOM for UTF-16) for every call.
 *     Typically, file based writers call {@link String#getBytes()} for each log entry. However, the charset header
 *     should only be written only at the start of a log file and not for each log entry.
 * </p>
 */
public class CharsetAdjustmentWriterDecorator implements ByteArrayWriter {

	private final ByteArrayWriter writer;
	private final byte[] charsetHeader;

	/**
	 * @param writer
	 *            Underlying writer
	 * @param charsetHeader
	 *            Charset header to skip
	 */
	public CharsetAdjustmentWriterDecorator(final ByteArrayWriter writer, final byte[] charsetHeader) {
		this.writer = writer;
		this.charsetHeader = Arrays.copyOf(charsetHeader, charsetHeader.length);
	}

	@Override
	public int readTail(final byte[] data, final int offset, final int length) throws IOException {
		return writer.readTail(data, offset, length);
	}

	@Override
	public void write(final byte[] data, final int length) throws IOException {
		write(data, 0, length);
	}

	@Override
	public void write(final byte[] data, final int offset, final int length) throws IOException {
		if (startsWithCharsetHeader(data, offset, length)) {
			writer.write(data, offset + charsetHeader.length, length - charsetHeader.length);
		} else {
			writer.write(data, offset, length);
		}
	}

	@Override
	public void truncate(final int count) throws IOException {
		writer.truncate(count);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	/**
	 * Checks if a byte array starts with the assigned charset header.
	 *
	 * @param data
	 *            Byte array to output
	 * @param offset
	 *            Start offset to output
	 * @param length
	 *            Number of bytes to output
	 * @return {@code true} if the passed byte array starts with the assigned charset header, {@code false} if not
	 */
	private boolean startsWithCharsetHeader(final byte[] data, final int offset, final int length) {
		if (charsetHeader.length > length) {
			return false;
		}

		for (int i = 0; i < charsetHeader.length; ++i) {
			if (charsetHeader[i] != data[offset + i]) {
				return false;
			}
		}

		return true;
	}

}
