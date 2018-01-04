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
import org.tinylog.runtime.RuntimeProvider;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProcessIdSegment}.
 */
public final class ProcessIdSegmentTest {

	private static final int PROCESS_ID = RuntimeProvider.getProcessId();

	/**
	 * Temporary folder for testing folders and files.
	 */
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	/**
	 * Verifies that never a latest file will be returned for continuing.
	 * 
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void getLatestFile() throws IOException {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;
		assertThat(new File(prefix + PROCESS_ID).createNewFile()).isTrue();

		Segment segment = new ProcessIdSegment(null);
		assertThat(segment.getLatestFile(prefix)).isNull();
	}

	/**
	 * Verifies that all files will be found that ends with a valid process ID.
	 * 
	 * @throws IOException
	 *             Failed creating temporary files
	 */
	@Test
	public void getAllFilesWithoutSuccessor() throws IOException {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		assertThat(new File(prefix + "123").createNewFile()).isTrue();
		assertThat(new File(prefix + "test").createNewFile()).isTrue();
		assertThat(new File(prefix + "test$").createNewFile()).isTrue();
		assertThat(new File(prefix + "test1").createNewFile()).isTrue();
		assertThat(new File(prefix + "test1A").createNewFile()).isTrue();
		assertThat(new File(prefix + "test123").createNewFile()).isTrue();
		assertThat(new File(prefix + "testA").createNewFile()).isTrue();

		Segment segment = new ProcessIdSegment(null);
		assertThat(segment.getAllFiles(prefix + "test")).containsOnly(prefix + "test1", prefix + "test123");
	}

	/**
	 * Verifies that all files will be found that contain a valid process ID and ends with the expected token.
	 * 
	 * @throws IOException
	 *             Failed creating temporary files
	 */
	@Test
	public void getAllFilesWithSuccessor() throws IOException {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		assertThat(new File(prefix + "123").createNewFile()).isTrue();
		assertThat(new File(prefix + "test").createNewFile()).isTrue();
		assertThat(new File(prefix + "test$").createNewFile()).isTrue();
		assertThat(new File(prefix + "test1").createNewFile()).isTrue();
		assertThat(new File(prefix + "test1A").createNewFile()).isTrue();
		assertThat(new File(prefix + "test123").createNewFile()).isTrue();
		assertThat(new File(prefix + "testA").createNewFile()).isTrue();

		Segment segment = new ProcessIdSegment(new StaticSegment("A", null));
		assertThat(segment.getAllFiles(prefix + "test")).containsOnly(prefix + "test1A");
	}

	/**
	 * Verifies that the collection of all files is empty for an empty folder.
	 */
	@Test
	public void getAllFilesForEmptyFolder() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		Segment segment = new ProcessIdSegment(null);
		assertThat(segment.getAllFiles(prefix + "test")).isEmpty();
	}

	/**
	 * Verifies that the collection of all files is empty for a non-existent folder.
	 */
	@Test
	public void getAllFilesForNonExistentFolder() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator + "dummy" + File.separator;

		Segment segment = new ProcessIdSegment(null);
		assertThat(segment.getAllFiles(prefix + "test")).isEmpty();
	}

	/**
	 * Verifies that process ID will be a added to path for new file.
	 */
	@Test
	public void createNewFile() {
		String prefix = temporaryFolder.getRoot().getAbsolutePath() + File.separator;

		Segment segment = new ProcessIdSegment(null);
		assertThat(segment.createNewFile(prefix)).isEqualTo(prefix + PROCESS_ID);
	}

}
