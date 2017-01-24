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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BufferedWriterDecorator}.
 */
public final class BufferedWriterDecoratorTest {

	private static final int BUFFER_CAPACITY = 64 * 1024;

	private BufferedWriterDecorator writer;
	private ByteArrayOutputStream stream;

	/**
	 * Creates a new instance of {@link BufferedWriterDecorator}. Output data will be stored in a
	 * {@link ByteArrayOutputStream}.
	 */
	@Before
	public void init() {
		stream = new ByteArrayOutputStream();
		writer = new BufferedWriterDecorator(new OutputStreamWriter(stream));
	}

	/**
	 * Verifies that written data will be available after closing writer.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	@Test
	public void writing() throws IOException {
		writer.write(new byte[] { 1, 2 }, 2);
		writer.write(new byte[] { 3 }, 1);
		writer.close();

		assertThat(stream.toByteArray()).startsWith((byte) 1, (byte) 2, (byte) 3);
	}

	/**
	 * Verifies that data will be written by exceeding the buffer capacity or invoking
	 * {@link BufferedWriterDecorator#flush()}.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	@Test
	public void flushing() throws IOException {
		writer.write(new byte[] { 1 }, 1);
		writer.write(new byte[BUFFER_CAPACITY - 2], BUFFER_CAPACITY - 2);
		assertThat(stream.toByteArray()).isEmpty();

		writer.write(new byte[] { 2 }, 1);
		writer.write(new byte[] { 3 }, 1);
		assertThat(stream.toByteArray()).startsWith((byte) 1).endsWith((byte) 2).hasSize(BUFFER_CAPACITY);

		writer.flush();
		assertThat(stream.toByteArray()).startsWith((byte) 1).endsWith((byte) 2, (byte) 3).hasSize(BUFFER_CAPACITY + 1);
	}

	/**
	 * Verifies that byte arrays bigger than the internal buffer capacity can be output completely.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	@Test
	public void dataBiggerThanBuffer() throws IOException {
		byte[] data = new byte[BUFFER_CAPACITY + 1];
		data[0] = 1;
		data[BUFFER_CAPACITY - 1] = 2;
		data[BUFFER_CAPACITY] = 3;

		writer.write(data, data.length);
		writer.close();

		assertThat(stream.toByteArray()).isEqualTo(data);
	}

}
