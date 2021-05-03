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
 * Thread-safe decorator for any {@link ByteArrayWriter} implementation.
 */
public final class SynchronizedWriterDecorator implements ByteArrayWriter {

	private final ByteArrayWriter writer;
	private final Object mutex;

	/**
	 * @param writer
	 *            Underlying writer
	 * @param mutex
	 *            Mutex for synchronized
	 */
	public SynchronizedWriterDecorator(final ByteArrayWriter writer, final Object mutex) {
		this.writer = writer;
		this.mutex = mutex;
	}

	@Override
	public int readTail(final byte[] data, final int offset, final int length) throws IOException {
		synchronized (mutex) {
			return writer.readTail(data, offset, length);
		}
	}

	@Override
	public void write(final byte[] data, final int length) throws IOException {
		write(data, 0, length);
	}

	@Override
	public void write(final byte[] data, final int offset, final int length) throws IOException {
		synchronized (mutex) {
			writer.write(data, offset, length);
		}
	}

	@Override
	public void truncate(final int count) throws IOException {
		synchronized (mutex) {
			writer.truncate(count);
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized (mutex) {
			writer.flush();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (mutex) {
			writer.close();
		}
	}

}
