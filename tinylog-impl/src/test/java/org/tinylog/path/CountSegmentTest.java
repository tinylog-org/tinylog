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
 * Tests for {@link CountSegment}.
 */
public final class CountSegmentTest {

	/**
	 * Temporary folder for creating volatile files.
	 */
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Verifies that there is no static text.
	 */
	@Test
	public void doesNotHaveStaticText() {
		CountSegment segment = new CountSegment();
		assertThat(segment.getStaticText()).isNull();
	}

	/**
	 * Verifies that "0" (zero) will be generated as token, if the target folder doesn't exist yet.
	 */
	@Test
	public void createTokenForNonExistentFolder() {
		CountSegment segment = new CountSegment();
		String prefix = folder.getRoot() + File.separator + "test" + File.separator;
		assertThat(segment.createToken(prefix, null)).isEqualTo("0");
	}

	/**
	 * Verifies that "0" (zero) will be generated as token, if the target folder is empty.
	 *
	 * @throws IOException
	 *             Failed to create folder
	 */
	@Test
	public void createTokenForEmptyFolder() throws IOException {
		CountSegment segment = new CountSegment();
		String prefix = folder.newFolder().getAbsolutePath() + File.separator;
		assertThat(segment.createToken(prefix, null)).isEqualTo("0");
	}

	/**
	 * Verifies that a sequence of numeric file names can be continued and the next number will be returned as token.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void createTokenForExistentFiles() throws IOException {
		folder.newFile("10");
		folder.newFile("11");
		folder.newFile("9");

		CountSegment segment = new CountSegment();
		String prefix = folder.getRoot().getAbsolutePath() + File.separator;
		assertThat(segment.createToken(prefix, null)).isEqualTo("12");
	}

	/**
	 * Verifies that a sequence from file names that contain a number can be continued and the next number will be
	 * returned as token.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void createTokenForRealFiles() throws IOException {
		folder.newFile("test10.log");
		folder.newFile("test11.log");
		folder.newFile("test9.log");
		folder.newFile("testA.log");
		folder.newFile("other42.log");

		CountSegment segment = new CountSegment();
		String prefix = folder.getRoot().getAbsolutePath() + File.separator + "test";
		assertThat(segment.createToken(prefix, null)).isEqualTo("12");
	}

	/**
	 * Verifies that "0" (zero) will be generated as token, if the target folder contains only files whose filenames do
	 * not contain a number at the expected position.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void createTokenForInvalidFiles() throws IOException {
		folder.newFile("abc");
		folder.newFile("test");

		CountSegment segment = new CountSegment();
		String prefix = folder.getRoot().getAbsolutePath() + File.separator;
		assertThat(segment.createToken(prefix, null)).isEqualTo("0");
	}

	/**
	 * Verifies that a sequence from a file from a relative path can be continued and the next number will be returned
	 * as token.
	 *
	 * @throws IOException
	 *             Failed to create file
	 */
	@Test
	public void createTokenInCurrentPath() throws IOException {
		File file = new File("41.tmp");
		try {
			file.createNewFile();
			CountSegment segment = new CountSegment();
			assertThat(segment.createToken("", null)).isEqualTo("42");
		} finally {
			file.delete();
		}
	}

	/**
	 * Verifies that a number will be accepted as valid token.
	 */
	@Test
	public void validateValidToken() {
		CountSegment segment = new CountSegment();
		assertThat(segment.validateToken("42")).isTrue();
	}

	/**
	 * Verifies that a non-numeric string will be not accepted as token.
	 */
	@Test
	public void validateInvalidToken() {
		CountSegment segment = new CountSegment();
		assertThat(segment.validateToken("abc")).isFalse();
	}

}
