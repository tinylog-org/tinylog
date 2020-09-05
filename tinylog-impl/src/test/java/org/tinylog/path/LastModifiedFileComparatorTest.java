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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LastModifiedFileTupleComparator}.
 */

public final class LastModifiedFileComparatorTest {

	private static final LastModifiedFileTupleComparator COMPARATOR = LastModifiedFileTupleComparator.INSTANCE;

	/**
	 * Verifies that zero will be returned, if both file tuples have the same last modification date.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void sameModificationDate() throws IOException {
		File firstFile = File.createTempFile("junit", null);
		File secondFile = File.createTempFile("junit", null);
		secondFile.setLastModified(firstFile.lastModified());

		FileTuple firstTuple = new FileTuple(firstFile, firstFile);
		FileTuple secondTuple = new FileTuple(secondFile, secondFile);

		assertThat(COMPARATOR.compare(firstTuple, secondTuple)).isZero();
	}

	/**
	 * Verifies that a negative number will be returned, if the first file tuple is younger than the second.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void keepYoungestFirst() throws IOException {
		ZonedDateTime now = ZonedDateTime.now();

		File firstFile = File.createTempFile("junit", null);
		File secondFile = File.createTempFile("junit", null);
		firstFile.setLastModified(now.toEpochSecond());
		secondFile.setLastModified(now.minus(1, ChronoUnit.DAYS).toEpochSecond());

		FileTuple firstTuple = new FileTuple(firstFile, firstFile);
		FileTuple secondTuple = new FileTuple(secondFile, secondFile);

		assertThat(COMPARATOR.compare(firstTuple, secondTuple)).isNegative();

	}

	/**
	 * Verifies that a positive number will be returned, if the second file tuple is older than the second.
	 *
	 * @throws IOException
	 *             Failed to create files
	 */
	@Test
	public void reorderIfOldestIsFirst() throws IOException {
		ZonedDateTime now = ZonedDateTime.now();

		File firstFile = File.createTempFile("junit", null);
		File secondFile = File.createTempFile("junit", null);
		firstFile.setLastModified(now.minus(1, ChronoUnit.DAYS).toEpochSecond());
		secondFile.setLastModified(now.toEpochSecond());

		FileTuple firstTuple = new FileTuple(firstFile, firstFile);
		FileTuple secondTuple = new FileTuple(secondFile, secondFile);

		assertThat(COMPARATOR.compare(firstTuple, secondTuple)).isPositive();
	}

}
