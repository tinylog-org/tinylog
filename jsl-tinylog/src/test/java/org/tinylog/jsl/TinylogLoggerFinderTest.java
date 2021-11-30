/*
 * Copyright 2021 Gerrit Rode
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

package org.tinylog.jsl;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TinylogLoggerFinder}.
 */
public class TinylogLoggerFinderTest {

	private TinylogLoggerFinder finder;

	/**
	 * Creates an instance of {@link TinylogLoggerFinder}.
	 */
	@Before
	public void init() {
		finder = new TinylogLoggerFinder();
	}

	/**
	 * Verifies that a logger instance of {@link TinylogLogger} will be created.
	 */
	@Test
	public void newLoggerInstance() {
		assertThat(finder.getLogger(TinylogLoggerFinderTest.class.getName(), TinylogLoggerFinderTest.class.getModule()))
			.isInstanceOf(TinylogLogger.class);
	}

	/**
	 * Verifies that logger instances are cached and will be reused.
	 */
	@Test
	public void cachedLoggerInstance() {
		System.Logger first = finder.getLogger(TinylogLoggerFinderTest.class.getName(), TinylogLoggerFinderTest.class.getModule());
		System.Logger second = finder.getLogger(TinylogLoggerFinderTest.class.getName(), TinylogLoggerFinderTest.class.getModule());
		assertThat(first).isSameAs(second);
	}

	/**
	 * Verifies that different logger instanced will be returned for different names.
	 */
	@Test
	public void differentLoggerInstance() {
		System.Logger first = finder.getLogger("org.test.FirstClass", TinylogLoggerFinderTest.class.getModule());
		System.Logger second = finder.getLogger("org.test.SecondClass", TinylogLoggerFinderTest.class.getModule());
		assertThat(first).isNotSameAs(second);
	}
}
