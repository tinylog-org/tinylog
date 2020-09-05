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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Tests for {@link FileTuple}.
 */
public final class FileTupleTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

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

	/**
	 * Verifies that the original file can be deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void deleteOriginalFile() throws IOException {
		File file = new File(FileSystem.createTemporaryFile());
		new FileTuple(file, new File(file.getPath() + ".backup")).delete();

		assertThat(file).doesNotExist();
	}

	/**
	 * Verifies that the backup file can be deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void deleteBackupFile() throws IOException {
		File file = new File(FileSystem.createTemporaryFile());
		new FileTuple(new File(file.getPath() + ".log"), file).delete();

		assertThat(file).doesNotExist();
	}

	/**
	 * Verifies that the original and backup file can be deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void deleteBothFiles() throws IOException {
		File originalFile = new File(FileSystem.createTemporaryFile());
		File backupFile = new File(FileSystem.createTemporaryFile());

		new FileTuple(originalFile, backupFile).delete();

		assertThat(originalFile).doesNotExist();
		assertThat(backupFile).doesNotExist();
	}

	/**
	 * Verifies that a warning will be output on Windows, if the original file cannot be deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void warnIfDeletingOriginalFileFailedOnWindows() throws IOException {
		assumeTrue(System.getProperty("os.name").startsWith("Windows"));

		File originalFile = new File(FileSystem.createTemporaryFile());
		File backupFile = new File(FileSystem.createTemporaryFile());

		try (FileInputStream stream = new FileInputStream(originalFile)) {
			new FileTuple(originalFile, backupFile).delete();
		}

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARN")
			.containsOnlyOnce(originalFile.getPath());
		assertThat(originalFile).exists();
		assertThat(backupFile).doesNotExist();
	}

	/**
	 * Verifies that a warning will be output on Windows, if the backup file cannot be deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void warnIfDeletingBackupFileFailedOnWindows() throws IOException {
		assumeTrue(System.getProperty("os.name").startsWith("Windows"));

		File originalFile = new File(FileSystem.createTemporaryFile());
		File backupFile = new File(FileSystem.createTemporaryFile());

		try (FileInputStream stream = new FileInputStream(backupFile)) {
			new FileTuple(originalFile, backupFile).delete();
		}

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARN")
			.containsOnlyOnce(backupFile.getPath());
		assertThat(originalFile).doesNotExist();
		assertThat(backupFile).exists();
	}

	/**
	 * Verifies that a warning will be output on POSIX compatible operating systems, if the original file cannot be
	 * deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void warnIfDeletingOriginalFileFailedOnPosix() throws IOException {
		assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));

		Path originalFolder = Files.createTempDirectory("original");
		originalFolder.toFile().deleteOnExit();
		File originalFile = Files.createFile(originalFolder.resolve("log.txt")).toFile();

		Path backupFolder = Files.createTempDirectory("backup");
		backupFolder.toFile().deleteOnExit();
		File backupFile = Files.createFile(backupFolder.resolve("log.backup")).toFile();

		try {
			originalFolder.toFile().setWritable(false);
			new FileTuple(originalFile, backupFile).delete();
		} finally {
			originalFolder.toFile().setWritable(true);
		}

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARN")
			.containsOnlyOnce(originalFile.getPath());
		assertThat(originalFile).exists();
		assertThat(backupFile).doesNotExist();

	}

	/**
	 * Verifies that a warning will be output on POSIX compatible operating systems, if the backup file cannot be
	 * deleted.
	 *
	 * @throws IOException
	 *             Failed access to temporary files
	 */
	@Test
	public void warnIfDeletingBackupFileFailedOnPosix() throws IOException {
		assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));

		Path originalFolder = Files.createTempDirectory("original");
		originalFolder.toFile().deleteOnExit();
		File originalFile = Files.createFile(originalFolder.resolve("log.txt")).toFile();

		Path backupFolder = Files.createTempDirectory("backup");
		backupFolder.toFile().deleteOnExit();
		File backupFile = Files.createFile(backupFolder.resolve("log.backup")).toFile();

		try {
			backupFolder.toFile().setWritable(false);
			new FileTuple(originalFile, backupFile).delete();
		} finally {
			backupFolder.toFile().setWritable(true);
		}

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARN")
			.containsOnlyOnce(backupFile.getPath());
		assertThat(originalFile).doesNotExist();
		assertThat(backupFile).exists();
	}

}
