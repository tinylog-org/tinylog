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

package org.tinylog.core.formats;

import java.util.Locale;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberFormatBuilderTest {

	/**
	 * Verifies that the builder is compatible with the current environment.
	 */
	@Test
	void compatibility() {
		NumberFormatBuilder builder = new NumberFormatBuilder();
		assertThat(builder.isCompatible()).isTrue();
	}

	/**
	 * Verifies that the builder can create an instance of {@link NumberFormat}.
	 */
	@Test
	void creation() {
		NumberFormatBuilder builder = new NumberFormatBuilder();
		assertThat(builder.create(Locale.GERMANY)).isInstanceOf(NumberFormat.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(ValueFormatBuilder.class))
			.anyMatch(builder -> builder instanceof NumberFormatBuilder);
	}

}
