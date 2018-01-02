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

package org.tinylog.policies;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SizePolicy}.
 */
@RunWith(Enclosed.class)
public final class SizePolicyTest {

	/**
	 * Base tests with static size argument.
	 */
	public static final class Base {

		/**
		 * Verifies that an illegal argument exception will be thrown if no size argument has been passed.
		 */
		@Test
		public void missingArgument() {
			assertThatThrownBy(() -> new SizePolicy(null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("size");
		}

		/**
		 * Verifies that an illegal argument exception will be thrown if an empty size argument has been passed.
		 */
		@Test
		public void emptyArgument() {
			assertThatThrownBy(() -> new SizePolicy("")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("size");
		}

		/**
		 * Verifies that an illegal argument exception will be thrown if a negative number has been passed as size.
		 */
		@Test
		public void negativeSize() {
			assertThatThrownBy(() -> new SizePolicy("-1")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("-1");
		}

		/**
		 * Verifies that an illegal argument exception will be thrown if an illegal number has been passed as size.
		 */
		@Test
		public void invalidSize() {
			assertThatThrownBy(() -> new SizePolicy("ABC")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ABC");
		}

		/**
		 * Verifies that policy is registered as service under the name "size".
		 */
		@Test
		public void isRegistered() {
			Policy policy = new ServiceLoader<>(Policy.class, String.class).create("size", "1MB");
			assertThat(policy).isInstanceOf(SizePolicy.class);
		}

	}

	/**
	 * Tests with dynamic size argument for testing all supported size units.
	 */
	@RunWith(Parameterized.class)
	public static final class Sizes {

		private final String argument;
		private final int size;

		/**
		 * @param argument
		 *            Argument for size policy
		 * @param size
		 *            Expected parsed file size
		 */
		public Sizes(final String argument, final int size) {
			this.argument = argument;
			this.size = size;
		}

		/**
		 * Returns dynamic file sizes that should be tested.
		 * 
		 * @return Each object array contains the textual size argument and the expected parsed file size
		 */
		@Parameters(name = "{0}")
		public static Collection<Object[]> getSizes() {
			List<Object[]> sizes = new ArrayList<>();
			sizes.add(new Object[] { "42", 42 });
			sizes.add(new Object[] { "1 bytes", 1 });
			sizes.add(new Object[] { "1 KB", 1024 });
			sizes.add(new Object[] { "1 MB", 1024 * 1024 });
			sizes.add(new Object[] { "1 GB", 1024 * 1024 * 1024 });
			return sizes;
		}

		/**
		 * Verifies that an existing log file will be not continued, if it is larger than the defined maximum file size.
		 * 
		 * @throws IOException
		 *             Failed creating temporary file
		 */
		@Test
		public void discontinueExistingFile() throws IOException {
			String file = createTemporaryFile(size + 1);
			Policy policy = new SizePolicy(argument);
			assertThat(policy.continueExistingFile(file)).isFalse();
		}

		/**
		 * Verifies continuing of an existing log file that is smaller than the defined maximum file size.
		 * 
		 * @throws IOException
		 *             Failed creating temporary file
		 */
		@Test
		public void continueExistingFile() throws IOException {
			String file = createTemporaryFile(size - 1);
			Policy policy = new SizePolicy(argument);
			assertThat(policy.continueExistingFile(file)).isTrue();
			assertThat(policy.continueCurrentFile(new byte[1])).isTrue();
			assertThat(policy.continueCurrentFile(new byte[1])).isFalse();
			policy.reset();
			assertThat(policy.continueCurrentFile(new byte[1])).isTrue();
		}

		/**
		 * Creates a new temporary file. The created file will be deleted automatically when the virtual machine
		 * terminates.
		 *
		 * @param size
		 *            Initial file size of created file
		 * @return Path to created file
		 * @throws IOException
		 *             Failed creating file
		 */
		private static String createTemporaryFile(final int size) throws IOException {
			String file = FileSystem.createTemporaryFile();
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
				randomAccessFile.setLength(size);
			}
			return file;
		}

	}

}
