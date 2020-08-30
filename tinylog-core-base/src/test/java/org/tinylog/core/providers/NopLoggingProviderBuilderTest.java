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

package org.tinylog.core.providers;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;

import static org.assertj.core.api.Assertions.assertThat;

class NopLoggingProviderBuilderTest {

	/**
	 * Verifies that the name is "nop".
	 */
	@Test
	void name() {
		NopLoggingProviderBuilder builder = new NopLoggingProviderBuilder();
		assertThat(builder.getName()).isEqualTo("nop");
	}

	/**
	 * Verifies that an instance of {@link NopLoggingProvider} can be created.
	 */
	@Test
	void creation() {
		Framework framework = new Framework(false, false);
		NopLoggingProviderBuilder builder = new NopLoggingProviderBuilder();
		assertThat(builder.create(framework)).isInstanceOf(NopLoggingProvider.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(LoggingProviderBuilder.class))
			.anyMatch(builder -> builder instanceof NopLoggingProviderBuilder);
	}

}
