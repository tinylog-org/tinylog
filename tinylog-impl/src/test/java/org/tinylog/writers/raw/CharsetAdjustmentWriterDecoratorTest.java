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
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link CharsetAdjustmentWriterDecorator}.
 */
public class CharsetAdjustmentWriterDecoratorTest {

	private static final byte[] CHARSET_HEADER = { 'A', 'B' };

	/**
	 * Verifies that {@link ByteArrayWriter#readTail(byte[], int, int)} method of underlying writer will be invoked.
	 *
	 * @throws IOException
	 *             Reading failed
	 */
	@Test
	public void readTail() throws IOException {
		ByteArrayWriter mock = mock(ByteArrayWriter.class);

		byte[] data = new byte[0];
		new CharsetAdjustmentWriterDecorator(mock, CHARSET_HEADER).readTail(data, 1, 2);

		verify(mock).readTail(data, 1, 2);
	}

	/**
	 * Verifies that charset headers are detected and skipped correctly.
	 *
	 * @throws IOException
	 *             Failed invoking writer
	 */
	@Test
	public void writeDataWithHeader() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		CharsetAdjustmentWriterDecorator decorator = new CharsetAdjustmentWriterDecorator(writer, CHARSET_HEADER);

		decorator.write(new byte[] { 'A', 'B', 'C' }, 3);
		decorator.write(new byte[] { 'A', 'B', 'D', 'E' }, 4);
		decorator.write(new byte[] { 'Z', 'A', 'B', 'F' }, 1, 3);
		decorator.flush();
		decorator.close();

		assertThat(Files.readAllBytes(Paths.get(path))).containsExactly('C', 'D', 'E', 'F');
	}

	/**
	 * Verifies that byte arrays without charset headers are written untouched.
	 *
	 * @throws IOException
	 *             Failed invoking writer
	 */
	@Test
	public void writeDataWithoutHeader() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(randomAccessFile);
		CharsetAdjustmentWriterDecorator decorator = new CharsetAdjustmentWriterDecorator(writer, CHARSET_HEADER);

		decorator.write(new byte[] { 'C' }, 1);
		decorator.write(new byte[] { 'D', 'E', 'F' }, 3);
		decorator.write(new byte[] { 'A', 'B', 'C', 'D' }, 1, 3);
		decorator.flush();
		decorator.close();

		assertThat(Files.readAllBytes(Paths.get(path))).containsExactly('C', 'D', 'E', 'F', 'B', 'C', 'D');
	}

	/**
	 * Verifies that {@link ByteArrayWriter#truncate(int)} method of underlying writer will be invoked.
	 *
	 * @throws IOException
	 *             Resizing failed
	 */
	@Test
	public void truncate() throws IOException {
		ByteArrayWriter mock = mock(ByteArrayWriter.class);

		new CharsetAdjustmentWriterDecorator(mock, CHARSET_HEADER).truncate(42);

		verify(mock).truncate(42);
	}

}
