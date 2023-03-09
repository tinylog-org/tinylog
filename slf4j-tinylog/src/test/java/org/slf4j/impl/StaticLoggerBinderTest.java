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

package org.slf4j.impl;

import org.junit.Test;
import org.tinylog.slf4j.LegacyTinylogLoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StaticLoggerBinder}.
 */
public final class StaticLoggerBinderTest {

	/**
	 * Verifies that always the same instance is returned as singleton static logger binder.
	 */
	@Test
	public void singleton() {
		assertThat(StaticLoggerBinder.getSingleton())
			.isSameAs(StaticLoggerBinder.getSingleton());
	}

	/**
	 * Verifies that {@link LegacyTinylogLoggerFactory} is returned as logger factory.
	 */
	@Test
	public void instance() {
		assertThat(StaticLoggerBinder.getSingleton().getLoggerFactory())
			.isInstanceOf(LegacyTinylogLoggerFactory.class);
	}

	/**
	 * Verifies that the fully-qualified class name of {@link LegacyTinylogLoggerFactory} is returned as logger factory class
	 * name.
	 */
	@Test
	public void className() {
		assertThat(StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr())
			.isEqualTo(LegacyTinylogLoggerFactory.class.getName());
	}

}
