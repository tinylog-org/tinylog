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

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupPolicy}.
 */
public final class StartupPolicyTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that an existing log file will be never continued.
	 * 
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void discontinueExistingFile() throws IOException {
		String file = FileSystem.createTemporaryFile();
		assertThat(new StartupPolicy(null).continueExistingFile(file)).isFalse();
	}

	/**
	 * Verifies that the current log file will be always continued.
	 */
	@Test
	public void continueCurrentFile() {
		assertThat(new StartupPolicy(null).continueCurrentFile(new byte[0])).isTrue();
	}

	/**
	 * Verifies that the reset() method can be executed without throwing any exception.
	 */
	@Test
	public void resetIsCallable() {
		new StartupPolicy(null).reset();
	}

	/**
	 * Verifies that a warning will be output, if an argument is passed.
	 */
	@Test
	public void warnIfArgumentIsSet() {
		new StartupPolicy("test");
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("argument");
	}

	/**
	 * Verifies that policy is registered as service under the name "startup".
	 */
	@Test
	public void isRegistered() {
		Policy policy = new ServiceLoader<>(Policy.class, String.class).create("startup", (String) null);
		assertThat(policy).isInstanceOf(StartupPolicy.class);
	}

}
