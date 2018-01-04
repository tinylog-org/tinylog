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

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StaticSegment}.
 */
public final class StaticSegmentTest {

	/**
	 * Temporary folder for testing folders and files.
	 */
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	/**
	 * Verifies that an existing file will be accepted as latest file.
	 * 
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void getLatestFileForExistingFile() throws IOException {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;
		assertThat(new File(prefix + "test.log").createNewFile()).isTrue();

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.getLatestFile(prefix)).isEqualTo(prefix + "test.log");
	}

	/**
	 * Verifies that a non-existent file will be not accepted as latest file.
	 */
	@Test
	public void getLatestFileForNonExistentFile() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.getLatestFile(prefix)).isNull();
	}

	/**
	 * Verifies that a file of a non-existent folder will be not accepted as latest file.
	 */
	@Test
	public void getLatestFileForNonExistentFolder() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator + "dummy" + File.separator;

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.getLatestFile(prefix)).isNull();
	}

	/**
	 * Verifies that an existing file will be added to the collection of all existing files.
	 * 
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void getAllFilesForExistingFile() throws IOException {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;
		assertThat(new File(prefix + "test.log").createNewFile()).isTrue();

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.getAllFiles(prefix)).containsOnly(prefix + "test.log");
	}

	/**
	 * Verifies that a non-existent file will be not added to the collection of all existing files.
	 */
	@Test
	public void getAllFilesForNonExistentFile() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.getAllFiles(prefix)).isEmpty();
	}

	/**
	 * Verifies that a file of a non-existent folder will be not added to the collection of all existing files.
	 */
	@Test
	public void getAllFilesForNonExistentFolder() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator + "dummy" + File.separator;

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.getAllFiles(prefix)).isEmpty();
	}

	/**
	 * Verifies that any passed path will be accepted as new file.
	 */
	@Test
	public void createNewFile() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		Segment segment = new StaticSegment("test.log", null);
		assertThat(segment.createNewFile(prefix)).isEqualTo(prefix + "test.log");
	}

}
