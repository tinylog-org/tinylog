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

package org.tinylog.core.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;

import static org.assertj.core.api.Assertions.assertThat;

class InternalLoggingBackendBuilderTest {

	/**
	 * Verifies that the name is "internal".
	 */
	@Test
	void name() {
		InternalLoggingBackendBuilder builder = new InternalLoggingBackendBuilder();
		assertThat(builder.getName()).isEqualTo("internal");
	}

	/**
	 * Verifies that an instance of {@link InternalLoggingBackend} can be created.
	 */
	@Test
	void creation() {
		Framework framework = new Framework(false, false);
		InternalLoggingBackendBuilder builder = new InternalLoggingBackendBuilder();
		assertThat(builder.create(framework)).isInstanceOf(InternalLoggingBackend.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
			.anyMatch(builder -> builder instanceof InternalLoggingBackendBuilder);
	}

}
