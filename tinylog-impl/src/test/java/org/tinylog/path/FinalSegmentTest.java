/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FinalSegment}.
 */
public final class FinalSegmentTest {

	/**
	 * Verifies that an existing file will be accepted as latest file.
	 * 
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void getLatestFileForExistingPath() throws IOException {
		String file = FileSystem.createTemporaryFile();
		assertThat(FinalSegment.INSTANCE.getLatestFile(file)).isEqualTo(file);
	}

	/**
	 * Verifies that a non-existent file will be not accepted as latest file.
	 * 
	 * @throws IOException
	 *             Failed creating or deleting temporary file
	 */
	@Test
	public void getLatestFileForNonExistentPath() throws IOException {
		String file = FileSystem.createTemporaryFile();
		Files.delete(Paths.get(file));

		assertThat(FinalSegment.INSTANCE.getLatestFile(file)).isNull();
	}

	/**
	 * Verifies that an existing file will be added to the collection of all existing files.
	 * 
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void getAllFilesForExistingPath() throws IOException {
		String file = FileSystem.createTemporaryFile();
		assertThat(FinalSegment.INSTANCE.getAllFiles(file)).containsOnly(file);
	}

	/**
	 * Verifies that a non-existent file will be not added to the collection of all existing files.
	 * 
	 * @throws IOException
	 *             Failed creating or deleting temporary file
	 */
	@Test
	public void getAllFilesForNonExistentPath() throws IOException {
		String file = FileSystem.createTemporaryFile();
		Files.delete(Paths.get(file));

		assertThat(FinalSegment.INSTANCE.getAllFiles(file)).isEmpty();
	}

	/**
	 * Verifies that any passed path will be accepted as new file.
	 */
	@Test
	public void createNewFile() {
		assertThat(FinalSegment.INSTANCE.createNewFile("test.txt")).isEqualTo("test.txt");
	}

}
