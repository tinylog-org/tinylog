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
import org.slf4j.helpers.BasicMarkerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StaticMarkerBinder}.
 */
public final class StaticMarkerBinderTest {

	/**
	 * Verifies that {@link BasicMarkerFactory} is returned as marker factory.
	 */
	@Test
	public void instance() {
		assertThat(StaticMarkerBinder.SINGLETON.getMarkerFactory()).isInstanceOf(BasicMarkerFactory.class);
	}

	/**
	 * Verifies that the fully-qualified class name of {@link BasicMarkerFactory} is returned as marker factory class
	 * name.
	 */
	@Test
	public void className() {
		assertThat(StaticMarkerBinder.SINGLETON.getMarkerFactoryClassStr()).isEqualTo(BasicMarkerFactory.class.getName());
	}

}
