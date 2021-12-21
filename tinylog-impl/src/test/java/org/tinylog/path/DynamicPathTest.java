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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.runtime.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Tests for {@link DynamicPath}.
 */
@RunWith(PowerMockRunner.class)
public final class DynamicPathTest {

	/**
	 * Temporary folder for creating volatile files.
	 */
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Verifies that a static path without any patterns can be resolved.
	 *
	 * @throws IOException
	 *             Failed to create file
	 */
	@Test
	public void staticPath() throws IOException {
		File file = folder.newFile();
		DynamicPath path = new DynamicPath(file.getAbsolutePath());
		assertThat(path.resolve()).isEqualTo(file.getAbsolutePath());
	}

	/**
	 * Verifies that a path with a count pattern can be resolved.
	 */
	@Test
	public void countToken() {
		String pattern = new File(folder.getRoot(), "{count}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "0.log");
	}

	/**
	 * Verifies that a path with a date pattern with the default timestamp format can be resolved.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	@PrepareForTest(RuntimeProvider.class)
	public void defaultDateToken() throws Exception {
		setCurrentTime(LocalDateTime.of(1985, 6, 3, 12, 30, 55));

		String pattern = new File(folder.getRoot(), "{date}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "1985-06-03_12-30-55.log");
	}

	/**
	 * Verifies that a path with a date pattern with a custom timestamp format can be resolved.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	@PrepareForTest(RuntimeProvider.class)
	public void customDateToken() throws Exception {
		setCurrentTime(LocalDateTime.of(1985, 6, 3, 12, 30, 55));

		String pattern = new File(folder.getRoot(), "{date:yyyy}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "1985.log");
	}

	/**
	 * Verifies that a path with a process ID pattern can be resolved.
	 */
	@Test
	public void processIdToken() {
		String pattern = new File(folder.getRoot(), "{pid}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + RuntimeProvider.getProcessId() + ".log");
	}

	/**
	 * Verifies that a path with a log filename pattern can be resolved.
	 */
	@Test
	public void dynamicName() {
		String pattern = new File(folder.getRoot(), "{dynamic name}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "log.log");
		DynamicNameSegment.setDynamicName("foo");
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "foo.log");
		DynamicNameSegment.setDynamicName("bar");
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "bar.log");
	}

	/**
	 * Verifies that a path with a log filename pattern including initial value can be resolved.
	 */
	@Test
	public void dynamicNameParameter() {
		String pattern = new File(folder.getRoot(), "{dynamic name: foobar}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "foobar.log");
		DynamicNameSegment.setDynamicName("baz");
		assertThat(path.resolve()).isEqualTo(folder.getRoot() + File.separator + "baz.log");
	}

	/**
	 * Verifies that a path with an unknown pattern will be rejected.
	 */
	@Test
	public void invalidToken() {
		String pattern = new File(folder.getRoot(), "{dummy}.log").getAbsolutePath();
		assertThatThrownBy(() -> new DynamicPath(pattern)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("dummy");
	}

	/**
	 * Verifies that a path with a count and process ID pattern can be resolved.
	 */
	@Test
	public void multipleTokens() {
		DynamicPath path = new DynamicPath("{count}.{pid}");
		assertThat(path.resolve()).isEqualTo("0." + RuntimeProvider.getProcessId());
	}

	/**
	 * Verifies that a path is rejected if patterns are not separated by a static text.
	 */
	@Test
	public void nonSeparatedTokens() {
		String pattern = "{count}{pid}";
		assertThatThrownBy(() -> new DynamicPath(pattern)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(pattern);
	}

	/**
	 * Verifies that a path is rejected if a opening curly bracket is missing.
	 */
	@Test
	public void missingOpeningBracket() {
		assertThatThrownBy(() -> new DynamicPath("count}")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("count}");
		assertThatThrownBy(() -> new DynamicPath("count}-{pid}")).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("count}-{pid}");
	}

	/**
	 * Verifies that a path is rejected if a closing curly bracket is missing.
	 */
	@Test
	public void missingClosingBracket() {
		assertThatThrownBy(() -> new DynamicPath("{count")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("{count");
		assertThatThrownBy(() -> new DynamicPath("{count-{pid}")).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("{count-{pid}");
	}

	/**
	 * Verifies that no files will be returned for an empty folder.
	 */
	@Test
	public void getFilesForEmptyFolder() {
		String pattern = new File(folder.getRoot(), "{pid}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.getAllFiles(null)).isEmpty();
	}

	/**
	 * Verifies that all original files of a folder will be returned that are compatible with the configured dynamic path.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void getExistingOriginalFiles() throws IOException {
		File first = folder.newFile("1.log");
		File second = folder.newFile("42.log");
		folder.newFile("42.old");

		String pattern = new File(folder.getRoot(), "{pid}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);

		assertThat(path.getAllFiles(null))
			.extracting(FileTuple::getOriginal)
			.containsExactlyInAnyOrder(first, second);
	}

	/**
	 * Verifies that all backup files of a folder will be returned that are compatible with the configured dynamic path.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void getExistingBackupFiles() throws IOException {
		File first = folder.newFile("1.log");
		File second = folder.newFile("42.log");

		folder.newFile("1.log.backup");
		folder.newFile("42.log.backup");
		folder.newFile("42.old.backup");

		String pattern = new File(folder.getRoot(), "{pid}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);

		assertThat(path.getAllFiles(".backup"))
			.extracting(FileTuple::getOriginal)
			.containsExactlyInAnyOrder(first, second);
	}

	/**
	 * Verifies that all original and backup files of a folder will be returned that are compatible with the configured dynamic path.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void getAllExistingFiles() throws IOException {
		File first = folder.newFile("1.log");
		File second = folder.newFile("2.log");
		File third = folder.newFile("3.log");

		first.delete();
		folder.newFile("1.log.backup");
		folder.newFile("2.log.backup");

		String pattern = new File(folder.getRoot(), "{pid}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);

		assertThat(path.getAllFiles(".backup"))
			.extracting(FileTuple::getOriginal)
			.containsExactlyInAnyOrder(first, second, third);
	}

	/**
	 * Verifies that all files of sub folders will be returned that are compatible with the configured dynamic path.
	 *
	 * @throws IOException
	 *             Failed to create folder or files
	 */
	@Test
	public void getNestedFiles() throws IOException {
		File subFolder = folder.newFolder("2018");
		File file = new File(subFolder, "42.log");
		file.createNewFile();
		new File(subFolder, "42.old").createNewFile();

		String pattern = new File(folder.getRoot(), "{date:YYYY}" + File.separator + "{pid}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);

		assertThat(path.getAllFiles(null))
			.extracting(FileTuple::getOriginal)
			.containsExactly(file);
	}

	/**
	 * Verifies that all files of a folder will be returned that are compatible with a dynamic path that contains two
	 * tokens.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void getMultiTokenFiles() throws IOException {
		File first = folder.newFile("2018_1.log");
		File second = folder.newFile("2018_2.log");
		folder.newFile("2018+3.log");

		String pattern = new File(folder.getRoot(), "{date:YYYY}_{count}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);

		assertThat(path.getAllFiles(null))
			.extracting(FileTuple::getOriginal)
			.containsExactlyInAnyOrder(first, second);
	}

	/**
	 * Verifies that all found files are sorted correctly by last modification date. The youngest file should come first
	 * and the oldest last.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void getSortedFiles() throws IOException {
		ZonedDateTime now = ZonedDateTime.now();

		File first = folder.newFile("1.log");
		first.setLastModified(now.toEpochSecond());

		File second = folder.newFile("2.log");
		second.setLastModified(now.minus(1, ChronoUnit.DAYS).toEpochSecond());

		File third = folder.newFile("3.log");
		third.setLastModified(now.plus(1, ChronoUnit.DAYS).toEpochSecond());

		String pattern = new File(folder.getRoot(), "{count}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);

		assertThat(path.getAllFiles(null))
			.extracting(FileTuple::getOriginal)
			.containsExactlyInAnyOrder(third, first, second);
	}

	/**
	 * Verifies that a log file with a file extension can be validated.
	 */
	@Test
	public void validateFileWithExtension() {
		String pattern = new File(folder.getRoot(), "{count}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot(), "42.log"))).isTrue();
	}

	/**
	 * Verifies that a log file without a file extension can be validated.
	 */
	@Test
	public void validateFileWithoutExtension() {
		String pattern = new File(folder.getRoot(), "{count}").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot(), "42"))).isTrue();
	}

	/**
	 * Verifies that a log file with a static prefix can be validated.
	 */
	@Test
	public void validateFileWithPrefix() {
		String pattern = new File(folder.getRoot(), "log_{count}.txt").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot(), "log_42.txt"))).isTrue();
	}

	/**
	 * Verifies that a log file with with a timestamp and a count number can be validated.
	 */
	@Test
	public void validateFileWithMultiplePatterns() {
		String pattern = new File(folder.getRoot(), "{date:YYYY}_{count}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot(), "2018_0.log"))).isTrue();
	}

	/**
	 * Verifies that a log file without any folders in the path can be validated.
	 */
	@Test
	public void validateFileInCurrentDirectory() {
		DynamicPath path = new DynamicPath("log_{count}.tmp");
		assertThat(path.isValid(new File("log_42.tmp"))).isTrue();
	}

	/**
	 * Verifies that a log file with a relative folder can be validated.
	 */
	@Test
	public void validateFileWithRelativeFolder() {
		DynamicPath path = new DynamicPath("test/log_{count}.tmp");
		assertThat(path.isValid(new File("test/log_42.tmp"))).isTrue();
	}

	/**
	 * Verifies that a log file from a different folder will be refused.
	 */
	@Test
	public void validateFileFromOtherFolder() {
		String pattern = new File(folder.getRoot(), "{count}").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot().getParentFile(), "42"))).isFalse();
	}

	/**
	 * Verifies that a log file with a different separator between patterns will be refused.
	 */
	@Test
	public void validateFileWithWrongSeparator() {
		String pattern = new File(folder.getRoot(), "{date:YYYY}.{count}.log").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot(), "2018_42.log"))).isFalse();
	}

	/**
	 * Verifies that a log file with a wrong prefix will be refused.
	 */
	@Test
	public void validateFileWithWrongPrefix() {
		String pattern = new File(folder.getRoot(), "log_{count}.txt").getAbsolutePath();
		DynamicPath path = new DynamicPath(pattern);
		assertThat(path.isValid(new File(folder.getRoot(), "test_42.txt"))).isFalse();
	}

	/**
	 * Overrides the current date and time.
	 *
	 * @param date
	 *            New date and time
	 */
	private void setCurrentTime(final LocalDateTime date) {
		Instant instant = date.atZone(ZoneId.systemDefault()).toInstant();

		Timestamp timestamp = mock(Timestamp.class);
		when(timestamp.toDate()).thenReturn(Date.from(instant));
		when(timestamp.toInstant()).thenReturn(instant);

		spy(RuntimeProvider.class);
		when(RuntimeProvider.createTimestamp()).thenReturn(timestamp);
	}

}
