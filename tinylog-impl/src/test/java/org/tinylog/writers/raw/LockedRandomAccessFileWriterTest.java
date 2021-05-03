/*
 * Copyright 2017 Martin Winandy
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tinylog.util.FileSystem;
import org.tinylog.util.JvmProcessBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

/**
 * Tests for {@link LockedRandomAccessFileWriter}.
 */
public final class LockedRandomAccessFileWriterTest {

	private static final int NUMBER_OF_PROCESSES = 5;
	private static final int NUMBER_OF_LINES = 1000;

	private static final String LINE = "!!! Test Line !!! 1234567890 !!! qwertzuiopasdfghjklyxcvbnm !!!" + System.lineSeparator();
	private static final byte[] DATA = LINE.getBytes(Charset.defaultCharset());

	/**
	 * Verifies that stored data can be read from tail.
	 *
	 * @throws IOException Reading failed
	 */
	@Test
	public void reading() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile file = new RandomAccessFile(path, "rw");
		file.write(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

		LockedRandomAccessFileWriter writer = new LockedRandomAccessFileWriter(file);
		byte[] data = new byte[16];

		assertThat(writer.readTail(data, 2, 4)).isEqualTo(4);
		assertThat(data)
			.contains(6, atIndex(2))
			.contains(7, atIndex(3))
			.contains(8, atIndex(4))
			.contains(9, atIndex(5));

		assertThat(writer.readTail(data, 0, 16)).isEqualTo(10);
		assertThat(data).startsWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

		writer.close();
	}

	/**
	 * Verifies that a {@link FileOutputStream} is wrapped correctly.
	 *
	 * @throws IOException
	 *             Failed accessing temporary file
	 */
	@Test
	public void writingBySingleProcess() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
		LockedRandomAccessFileWriter writer = new LockedRandomAccessFileWriter(randomAccessFile);

		writer.write(new byte[] { 'A', 'B', 'C' }, 2);
		writer.write(new byte[] { 'D', 'E', 'F', 'G' }, 1, 2);
		writer.flush();
		writer.close();

		assertThat(FileSystem.readFile(path)).isEqualTo("ABEF");
	}

	/**
	 * Verifies that multiple processes can write simultaneously to the same file.
	 * 
	 * @throws IOException
	 *             Failed accessing temporary file or creating process
	 * @throws InterruptedException
	 *             Interrupted while waiting for process
	 */
	@Test
	public void writingByMultipleProcesses() throws IOException, InterruptedException {
		File file = new File(FileSystem.createTemporaryFile());
		String path = file.getAbsolutePath();

		if (!file.delete()) {
			throw new IOException("Failed to delete temporary file: " + path);
		}

		List<Process> processes = new JvmProcessBuilder(LockedRandomAccessFileWriterTest.class, path).start(NUMBER_OF_PROCESSES);

		if (!file.createNewFile()) {
			throw new IOException("Failed to recreate temporary file: " + path);
		}

		for (Process process : processes) {
			process.waitFor();
		}

		assertThat(FileSystem.readFile(path))
			.hasLineCount(NUMBER_OF_PROCESSES * NUMBER_OF_LINES)
			.matches("(" + Pattern.quote(LINE) + "){" + (NUMBER_OF_PROCESSES * NUMBER_OF_LINES) + "}");
	}

	/**
	 * Verifies that stored data can be shrunk.
	 *
	 * @throws IOException Resizing failed
	 */
	@Test
	public void shrinking() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile file = new RandomAccessFile(path, "rw");
		file.write(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });

		LockedRandomAccessFileWriter writer = new LockedRandomAccessFileWriter(file);
		writer.shrink(4);
		writer.close();

		assertThat(Files.readAllBytes(Paths.get(path))).containsExactly(0, 1, 2, 3, 4, 5);
	}

	/**
	 * Writes a defined number of lines to a given target file. This main method is used to test writing simultaneously
	 * to the same file by multiple processes.
	 * 
	 * @param arguments
	 *            First element will be used as file name for target file
	 * @throws IOException
	 *             Failed accessing target file
	 */
	public static void main(final String[] arguments) throws IOException {
		File file = new File(arguments[0]);
		while (!file.exists()) {
			Thread.yield();
		}

		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		LockedRandomAccessFileWriter writer = new LockedRandomAccessFileWriter(randomAccessFile);

		for (int i = 0; i < NUMBER_OF_LINES; ++i) {
			writer.write(DATA, 0, DATA.length);
		}

		writer.close();
	}

}
