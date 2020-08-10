/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.converters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Tests for {@link GzipEncoder}.
 */
public final class GzipEncoderTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Temporary folder for creating files.
	 */
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Verifies that a text file can be compressed and the original file will be deleted.
	 *
	 * @throws IOException
	 *             Failed to read or write file
	 */
	@Test
	public void encodeExistingFile() throws IOException {
		String originalText = "Hello tinylog! This is some flavor text.";
		File originalFile = folder.newFile();
		File compressedFile = new File(originalFile.getAbsolutePath() + ".gz");

		Files.write(originalFile.toPath(), originalText.getBytes(StandardCharsets.UTF_8));
		new GzipEncoder(originalFile).run();

		assertThat(compressedFile).isFile();
		assertThat(originalFile).doesNotExist();

		try (InputStream fileStream = new FileInputStream(compressedFile)) {
			try (GZIPInputStream gzipStream = new GZIPInputStream(fileStream)) {
				String uncompressedText = new String(gzipStream.readAllBytes(), StandardCharsets.UTF_8);
				assertThat(uncompressedText).isEqualTo(originalText);
			}
		}
	}

	/**
	 * Verifies that a meaningful error will be output, if the passed file does not exist.
	 *
	 * @throws IOException
	 *             Failed to read or write file
	 */
	@Test
	public void reportNonExistingFile() throws IOException {
		File file = folder.newFile();
		Files.delete(file.toPath());

		new GzipEncoder(file).run();

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.contains(file.getAbsolutePath());
	}

	/**
	 * Verifies that a meaningful warning will be output, if the passed file cannot be deleted.
	 *
	 * @throws IOException
	 *             Failed to read or write file
	 */
	@Test
	public void reportUndeletableFile() throws IOException {
		File file = spy(folder.newFile());
		Files.write(file.toPath(), "Test".getBytes(StandardCharsets.UTF_8));
		doReturn(false).when(file).delete();

		new GzipEncoder(file).run();

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARN")
			.containsOnlyOnce("delete")
			.contains(file.getAbsolutePath());
	}

}
