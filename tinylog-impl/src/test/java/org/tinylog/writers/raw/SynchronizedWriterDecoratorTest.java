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

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link SynchronizedWriterDecorator}.
 */
public final class SynchronizedWriterDecoratorTest {

	/**
	 * Verifies that {@code write()} method of underlying writer will be invoked.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	@Test
	public void write() throws IOException {
		ByteArrayWriter mock = mock(ByteArrayWriter.class);

		byte[] data = new byte[0];
		new SynchronizedWriterDecorator(mock, new Object()).write(data, 42);

		verify(mock).write(data, 42);
	}

	/**
	 * Verifies that {@code flush()} method of underlying writer will be invoked.
	 *
	 * @throws IOException
	 *             Flushing failed
	 */
	@Test
	public void flush() throws IOException {
		ByteArrayWriter mock = mock(ByteArrayWriter.class);

		new SynchronizedWriterDecorator(mock, new Object()).flush();

		verify(mock).flush();
	}

	/**
	 * Verifies that {@code close()} method of underlying writer will be invoked.
	 *
	 * @throws IOException
	 *             Closing failed
	 */
	@Test
	public void close() throws IOException {
		ByteArrayWriter mock = mock(ByteArrayWriter.class);

		new SynchronizedWriterDecorator(mock, new Object()).close();

		verify(mock).close();
	}

}
