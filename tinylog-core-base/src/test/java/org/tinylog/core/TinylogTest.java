/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.runtime.RuntimeFlavor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class TinylogTest {

	/**
	 * Verifies that a {@link RuntimeFlavor} is provided.
	 */
	@Test
	void runtime() {
		assertThat(Tinylog.getRuntime()).isNotNull();
	}

	/**
	 * Verifies that a {@link Configuration} is provided.
	 */
	@Test
	void configuration() {
		assertThat(Tinylog.getConfiguration()).isNotNull();
	}

	/**
	 * Verifies that a {@link LoggingBackend} is provided.
	 */
	@Test
	void loggingBackend() {
		assertThat(Tinylog.getLoggingBackend()).isNotNull();
	}

	/**
	 * Verifies that the life cycle works including hook registration.
	 */
	@Test
	void lifeCycle() {
		Hook hook = mock(Hook.class);
		Tinylog.registerHook(hook);

		try {
			Tinylog.startUp();
			Tinylog.removeHook(hook);
		} finally {
			Tinylog.shutDown();
		}

		verify(hook).startUp();
		verify(hook, never()).shutDown();
	}

}
