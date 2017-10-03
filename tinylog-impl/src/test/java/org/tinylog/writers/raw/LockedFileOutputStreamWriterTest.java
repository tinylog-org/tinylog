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
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tinylog.util.FileSystem;
import org.tinylog.util.JvmProcessBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LockedFileOutputStreamWriter}.
 */
public final class LockedFileOutputStreamWriterTest {

	private static final int NUMBER_OF_PROCESSES = 5;
	private static final int NUMBER_OF_LINES = 1000;

	private static final String LINE = "!!! Test Line !!! 1234567890 !!! qwertzuiopasdfghjklyxcvbnm !!!" + System.lineSeparator();
	private static final byte[] DATA = LINE.getBytes(Charset.defaultCharset());

	/**
	 * Verifies that a {@link FileOutputStream} is wrapped correctly.
	 *
	 * @throws IOException
	 *             Failed accessing temporary file
	 */
	@Test
	public void singleProcess() throws IOException {
		String fileName = FileSystem.createTemporaryFile();
		FileOutputStream stream = new FileOutputStream(fileName);
		LockedFileOutputStreamWriter writer = new LockedFileOutputStreamWriter(stream);

		writer.write(new byte[] { 'A', 'B', 'C' }, 2);
		writer.flush();
		writer.close();

		assertThat(FileSystem.readFile(fileName)).isEqualTo("AB");
		assertThat(stream.getChannel().isOpen()).isFalse();
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
	public void multipleProcesses() throws IOException, InterruptedException {
		File file = new File(FileSystem.createTemporaryFile());
		String path = file.getAbsolutePath();

		if (!file.delete()) {
			throw new IOException("Failed to delete temporary file: " + path);
		}

		List<Process> processes = new JvmProcessBuilder(LockedFileOutputStreamWriterTest.class, path).start(NUMBER_OF_PROCESSES);

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

		LockedFileOutputStreamWriter writer = new LockedFileOutputStreamWriter(new FileOutputStream(file, true));

		for (int i = 0; i < NUMBER_OF_LINES; ++i) {
			writer.write(DATA, DATA.length);
		}

		writer.close();
	}

}
