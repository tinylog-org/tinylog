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
	public void write(final byte[] data, final int length) throws IOException {
		if (position > 0 && BUFFER_CAPACITY - position < length) {
			writer.write(buffer, position);
			position = 0;
		}

		if (BUFFER_CAPACITY < length) {
			writer.write(data, length);
		} else {
			System.arraycopy(data, 0, buffer, position, length);
			position += length;
		}
	}

	@Override
	public void flush() throws IOException {
		if (position > 0) {
			writer.write(buffer, position);
			position = 0;
		}

		writer.flush();
	}

	@Override
	public void close() throws IOException {
		if (position > 0) {
			writer.write(buffer, position);
		}

		writer.close();
	}

}
