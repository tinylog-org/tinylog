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

package org.tinylog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaggedLoggerTest {

	/**
	 * Verifies that a string can be assigned as tag.
	 */
	@Test
	void stringTag() {
		TaggedLogger logger = new TaggedLogger("dummy");
		assertThat(logger.getTag()).isEqualTo("dummy");
	}

	/**
	 * Verifies that {@code null} can be passed as tag for creating an untagged logger.
	 */
	@Test
	void nullTag() {
		TaggedLogger logger = new TaggedLogger(null);
		assertThat(logger.getTag()).isNull();
	}

}
