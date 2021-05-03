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
 * Decorator to buffer output data for any {@link ByteArrayWriter} implementation. Data will be finally written if
 * either the buffer is full or {@link #flush()} or {@link #close()} is called.
 */
public final class BufferedWriterDecorator implements ByteArrayWriter {

	private static final int BUFFER_CAPACITY = 64 * 1024; // 64 KB

	private final ByteArrayWriter writer;
	private final byte[] buffer;
	private int position;

	/**
	 * @param writer
	 *            Underlying writer
	 */
	public BufferedWriterDecorator(final ByteArrayWriter writer) {
		this.writer = writer;
		this.buffer = new byte[BUFFER_CAPACITY];
		this.position = 0;
	}

	@Override
	public int readTail(final byte[] data, final int offset, final int length) throws IOException {
		if (length <= position) {
			System.arraycopy(buffer, position - length, data, offset, length);
			return length;
		} else {
			int readBytes = writer.readTail(data, offset, length - position);
			System.arraycopy(buffer, 0, data, offset + readBytes, position);
			return readBytes + position;
		}
	}

	@Override
	public void write(final byte[] data, final int length) throws IOException {
		write(data, 0, length);
	}

	@Override
	public void write(final byte[] data, final int offset, final int length) throws IOException {
		if (position > 0 && BUFFER_CAPACITY - position < length) {
			writer.write(buffer, 0, position);
			position = 0;
		}

		if (BUFFER_CAPACITY < length) {
			writer.write(data, offset, length);
		} else {
			System.arraycopy(data, offset, buffer, position, length);
			position += length;
		}
	}

	@Override
	public void truncate(final int count) throws IOException {
		if (count <= position) {
			position -= count;
		} else {
			writer.truncate(count - position);
			position = 0;
		}
	}

	@Override
	public void flush() throws IOException {
		if (position > 0) {
			writer.write(buffer, 0, position);
			position = 0;
		}

		writer.flush();
	}

	@Override
	public void close() throws IOException {
		if (position > 0) {
			writer.write(buffer, 0, position);
		}

		writer.close();
	}

}
