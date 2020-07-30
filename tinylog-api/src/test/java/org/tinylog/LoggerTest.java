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

class LoggerTest {

	/**
	 * Verifies that the same logger instance is returned for the same tag.
	 */
	@Test
	void sameLoggerInstanceForSameTag() {
		TaggedLogger first = Logger.tag("foo");
		TaggedLogger second = Logger.tag("foo");
		assertThat(first).isNotNull().isSameAs(second);
	}

	/**
	 * Verifies that different logger instances are returned for different tags.
	 */
	@Test
	void differentLoggerInstanceForDifferentTag() {
		TaggedLogger first = Logger.tag("foo");
		TaggedLogger second = Logger.tag("boo");

		assertThat(first).isNotNull();
		assertThat(second).isNotNull();
		assertThat(first).isNotSameAs(second);
	}

	/**
	 * Verifies that the same untagged root logger is returned for {@code null} and empty tags.
	 */
	@Test
	void sameUntaggedRootLoggerForNullAndEmptyTags() {
		TaggedLogger nullTag = Logger.tag(null);
		TaggedLogger emptyTag = Logger.tag("");

		assertThat(nullTag).isNotNull();
		assertThat(nullTag.getTag()).isNull();
		assertThat(emptyTag).isNotNull();
		assertThat(emptyTag.getTag()).isNull();

		assertThat(nullTag).isSameAs(emptyTag);
	}

}
