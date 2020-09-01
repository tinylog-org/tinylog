/*
 * Copyright $year Martin Winandy
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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FileTuple}.
 */
public final class FileTupleTest {

	/**
	 * Verifies that the last modification date of the original file will be used, if the backup file does not exist.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void originalFileOnly() throws IOException {
		File original = File.createTempFile("junit", null);
		File backup = File.createTempFile("junit", null);
		backup.delete();

		assertThat(new FileTuple(original, backup).getLastModified()).isEqualTo(original.lastModified());
	}

	/**
	 * Verifies that the last modification date of the backup file will be used, if the original file does not exist.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void backupFileOnly() throws IOException {
		File original = File.createTempFile("junit", null);
		File backup = File.createTempFile("junit", null);
		original.delete();

		assertThat(new FileTuple(original, backup).getLastModified()).isEqualTo(backup.lastModified());
	}

	/**
	 * Verifies that the last modification date of the original file will be used, if it is younger than the last modification date of the
	 * backup file.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void originalYoungerThanBackup() throws IOException {
		File original = File.createTempFile("junit", null);
		File backup = File.createTempFile("junit", null);

		original.setLastModified(2);
		backup.setLastModified(1);

		assertThat(new FileTuple(original, backup).getLastModified()).isEqualTo(original.lastModified());
	}

	/**
	 * Verifies that the last modification date of the backup file will be used, if it is younger than the last modification date of the
	 * original file.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void backupYoungerThanOriginal() throws IOException {
		File original = File.createTempFile("junit", null);
		File backup = File.createTempFile("junit", null);

		original.setLastModified(1);
		backup.setLastModified(2);

		assertThat(new FileTuple(original, backup).getLastModified()).isEqualTo(backup.lastModified());
	}

}
