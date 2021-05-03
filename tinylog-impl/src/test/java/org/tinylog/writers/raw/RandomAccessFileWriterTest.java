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
import java.nio.charset.Charset;

import org.junit.Test;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RandomAccessFileWriter}.
 */
public final class RandomAccessFileWriterTest {

	/**
	 * Verifies that written data will be available after writing and after closing
	 * the writer.
	 *
	 * @throws IOException Writing failed
	 */
	@Test
	public void writing() throws IOException {
		String path = FileSystem.createTemporaryFile();
		RandomAccessFile file = new RandomAccessFile(path, "rw");
		RandomAccessFileWriter writer = new RandomAccessFileWriter(file);

		writer.write(new byte[] { 1, 2, 3 }, 2);
		writer.write(new byte[] { 4, 5, 6, 7 }, 1, 2);

		byte[] writtenBytes = FileSystem.readFile(path).getBytes(Charset.defaultCharset());
		assertThat(writtenBytes).containsExactly((byte) 1, (byte) 2, (byte) 5, (byte) 6);

		writer.flush();
		writer.close();

		writtenBytes = FileSystem.readFile(path).getBytes(Charset.defaultCharset());
		assertThat(writtenBytes).containsExactly((byte) 1, (byte) 2, (byte) 5, (byte) 6);
	}

}
