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
import org.tinylog.configuration.ServiceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tinylog.util.ResultObserver.waitFor;

/**
 * Tests for {@link GzipFileConverter}.
 */
public class GzipFileConverterTest {

	/**
	 * Temporary folder for creating files.
	 */
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Verifies that ".gz" is provided as file extension for backup files.
	 */
	@Test
	public void suffix() {
		assertThat(new GzipFileConverter().getBackupSuffix()).isEqualTo(".gz");
	}

	/**
	 * Verifies that files will be compressed and the original file will be deleted after closing.
	 *
	 * @throws IOException
	 *             Failed to create new file
	 * @throws InterruptedException
	 *             Interrupted while waiting for the converter
	 */
	@Test
	public void compression() throws IOException, InterruptedException {
		File originalFile = folder.newFile();
		File compressedFile = new File(originalFile.getAbsolutePath() + ".gz");

		byte[] data = "My little GZIP test".getBytes(StandardCharsets.UTF_8);
		GzipFileConverter converter = new GzipFileConverter();

		converter.open(originalFile.getAbsolutePath());
		assertThat(converter.write(data)).isSameAs(data);
		Files.write(originalFile.toPath(), data);
		converter.close();
		converter.shutdown();

		waitFor(originalFile::exists, value -> !value, 1000);

		try (InputStream fileStream = new FileInputStream(compressedFile)) {
			try (GZIPInputStream gzipStream = new GZIPInputStream(fileStream)) {
				String uncompressedText = new String(gzipStream.readAllBytes(), StandardCharsets.UTF_8);
				assertThat(uncompressedText).isEqualTo("My little GZIP test");
			}
		}

		assertThat(originalFile).doesNotExist();
	}

	/**
	 * Verifies that the GZIP converter is registered as service under the name "gzip".
	 */
	@Test
	public void isRegistered() {
		FileConverter converter = new ServiceLoader<>(FileConverter.class).create("gzip");
		assertThat(converter).isInstanceOf(GzipFileConverter.class);
	}

}
