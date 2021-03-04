/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.impl.policy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SizePolicyTest {

	private Path logFile;

	/**
	 * Creates a temporary log file.
	 *
	 * @throws IOException Failed to create a temporary log file
	 */
	@BeforeEach
	void init() throws IOException {
		logFile = Files.createTempFile("tinylog", ".log");
		logFile.toFile().deleteOnExit();
	}

	/**
	 * Deletes the created temporary log file.
	 *
	 * @throws IOException Failed to delete the temporary log file
	 */
	@AfterEach
	void release() throws IOException {
		Files.deleteIfExists(logFile);
	}

	/**
	 * Verifies that the current log file can be continued, if the current file size is smaller than the defined maximum
	 * file size.
	 */
	@Test
	void continueUndersizedFile() throws IOException {
		increaseSizeOfLogFile(9);

		SizePolicy policy = new SizePolicy(100);
		assertThat(policy.canContinueFile(logFile)).isTrue();
	}

	/**
	 * Verifies that the current log file has to be discontinued, if the current file size is equal to the defined
	 * maximum file size.
	 */
	@Test
	void discontinueFullFile() throws IOException {
		increaseSizeOfLogFile(10);

		SizePolicy policy = new SizePolicy(10);
		assertThat(policy.canContinueFile(logFile)).isFalse();
	}

	/**
	 * Verifies that the current log file has to be discontinued, if the current file size is larger than the defined
	 * maximum file size.
	 */
	@Test
	void discontinueOversizedFile() throws IOException {
		increaseSizeOfLogFile(11);

		SizePolicy policy = new SizePolicy(10);
		assertThat(policy.canContinueFile(logFile)).isFalse();
	}

	/**
	 * Verifies that additional log entries are accepted until the defined maximum file size is reached.
	 */
	@Test
	void acceptLogEntriesUntilMaxSize() throws IOException {
		increaseSizeOfLogFile(2);

		SizePolicy policy = new SizePolicy(10);
		policy.init(logFile);

		assertThat(policy.canAcceptLogEntry(1)).isTrue();
		assertThat(policy.canAcceptLogEntry(7)).isTrue();
		assertThat(policy.canAcceptLogEntry(1)).isFalse();
	}

	/**
	 * Increase the size of the log file.
	 *
	 * <p>
	 *     The new file size will be the current size plus the passed number of bytes.
	 * </p>
	 *
	 * @param bytes The number of additional bytes for the log file
	 * @throws IOException Failed to access the log file
	 */
	private void increaseSizeOfLogFile(int bytes) throws IOException {
		try (OutputStream stream = Files.newOutputStream(logFile, StandardOpenOption.APPEND)) {
			stream.write(new byte[bytes]);
		}
	}

}
