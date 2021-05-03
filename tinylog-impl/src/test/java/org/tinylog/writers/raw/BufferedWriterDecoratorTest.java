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
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

/**
 * Tests for {@link BufferedWriterDecorator}.
 */
public final class BufferedWriterDecoratorTest {

	private static final int BUFFER_CAPACITY = 64 * 1024;

	/**
	 * Verifies that stored data can be read from tail.
	 *
	 * @throws IOException Reading failed
	 */
	@Test
	public void reading() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		BufferedWriterDecorator decorator = new BufferedWriterDecorator(writer);

		decorator.write(new byte[] { 0, 1, 2, 3, 4 }, 0, 5);
		decorator.flush();
		decorator.write(new byte[] { 5, 6, 7, 8, 9 }, 0, 5);

		byte[] data = new byte[8];

		assertThat(decorator.readTail(data, 2, 4)).isEqualTo(4);
		assertThat(data)
			.contains(6, atIndex(2))
			.contains(7, atIndex(3))
			.contains(8, atIndex(4))
			.contains(9, atIndex(5));

		assertThat(decorator.readTail(data, 0, 8)).isEqualTo(8);
		assertThat(data).startsWith(2, 3, 4, 5, 6, 7, 8, 9);

		decorator.close();
	}

	/**
	 * Verifies that written data will be available after closing writer.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	@Test
	public void writing() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		BufferedWriterDecorator decorator = new BufferedWriterDecorator(writer);

		decorator.write(new byte[] { 1, 2 }, 2);
		decorator.write(new byte[] { 3, 4, 5, 6 }, 1, 3);
		decorator.close();

		assertThat(Files.readAllBytes(Paths.get(path))).startsWith((byte) 1, (byte) 2, (byte) 4, (byte) 5, (byte) 6);
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
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		BufferedWriterDecorator decorator = new BufferedWriterDecorator(writer);

		decorator.write(new byte[] { 1 }, 0, 1);
		decorator.write(new byte[BUFFER_CAPACITY - 2], BUFFER_CAPACITY - 2);
		assertThat(Files.readAllBytes(Paths.get(path))).isEmpty();

		decorator.write(new byte[] { 2 }, 0, 1);
		decorator.write(new byte[] { 3 }, 0, 1);
		assertThat(Files.readAllBytes(Paths.get(path)))
			.startsWith((byte) 1)
			.endsWith((byte) 2)
			.hasSize(BUFFER_CAPACITY);

		decorator.flush();
		assertThat(Files.readAllBytes(Paths.get(path)))
			.startsWith((byte) 1)
			.endsWith((byte) 2, (byte) 3)
			.hasSize(BUFFER_CAPACITY + 1);
	}

	/**
	 * Verifies that byte arrays bigger than the internal buffer capacity can be output completely.
	 *
	 * @throws IOException
	 *             Writing failed
	 */
	@Test
	public void dataBiggerThanBuffer() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		BufferedWriterDecorator decorator = new BufferedWriterDecorator(writer);

		byte[] data = new byte[BUFFER_CAPACITY + 1];
		data[0] = 1;
		data[BUFFER_CAPACITY - 1] = 2;
		data[BUFFER_CAPACITY] = 3;

		decorator.write(data, 0, data.length);
		decorator.close();

		assertThat(Files.readAllBytes(Paths.get(path))).isEqualTo(data);
	}

	/**
	 * Verifies that stored data can be shrunk.
	 *
	 * @throws IOException Resizing failed
	 */
	@Test
	public void shrinking() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		BufferedWriterDecorator decorator = new BufferedWriterDecorator(writer);

		decorator.write(new byte[] { 0, 1, 2, 3, 4 }, 0, 5);
		decorator.flush();
		decorator.write(new byte[] { 5, 6, 7, 8, 9 }, 0, 5);

		byte[] data = new byte[16];

		decorator.shrink(4);
		assertThat(decorator.readTail(data, 0, 16)).isEqualTo(6);
		assertThat(data).startsWith(0, 1, 2, 3, 4, 5);

		decorator.shrink(4);
		assertThat(decorator.readTail(data, 0, 16)).isEqualTo(2);
		assertThat(data).startsWith(0, 1);

		decorator.close();
	}

}
