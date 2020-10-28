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

package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeProviderTest {

	/**
	 * Verifies that an {@link AndroidRuntime} is provided on Android.
	 */
	@Test
	@EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	void androidRuntime() {
		RuntimeProvider backend = new RuntimeProvider();
		assertThat(backend.getRuntime()).isInstanceOf(AndroidRuntime.class);
	}

	/**
	 * Verifies that a {@link JavaRuntime} is provided on standard Java.
	 */
	@Test
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	void legacyJavaRuntime() {
		RuntimeProvider backend = new RuntimeProvider();
		assertThat(backend.getRuntime()).isInstanceOf(JavaRuntime.class);
	}

}
