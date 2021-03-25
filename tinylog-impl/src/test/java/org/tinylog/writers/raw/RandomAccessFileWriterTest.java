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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Before;
import org.junit.Test;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RandomAccessFileWriter}.
 */
public final class RandomAccessFileWriterTest {

	private RandomAccessFileWriter writer;
	private String filePath;
	private RandomAccessFile file;

	/**
	 * Creates a new instance of {@link RandomAccessFileWriter}. Output data will be
	 * written in a {@link RandomAccessFile}.
	 * 
	 * @throws IOException           Failed creating file
	 * @throws FileNotFoundException Could not find file
	 */
	@Before
	public void init() throws FileNotFoundException, IOException {
		filePath = FileSystem.createTemporaryFile();
		file = new RandomAccessFile(filePath, "rw");
		writer = new RandomAccessFileWriter(file);
	}

	/**
	 * Verifies that written data will be available after writing and after closing
	 * the writer.
	 *
	 * @throws IOException Writing failed
	 */
	@Test
	public void writing() throws IOException {
		writer.write(new byte[] { 1, 2 }, 2);
		writer.write(new byte[] { 3 }, 1);
		byte[] writtenBytes = FileSystem.readFile(filePath).getBytes();
		assertThat(writtenBytes).startsWith((byte) 1, (byte) 2, (byte) 3);
		writer.close();
		writtenBytes = FileSystem.readFile(filePath).getBytes();
		assertThat(writtenBytes).startsWith((byte) 1, (byte) 2, (byte) 3);
	}

}
