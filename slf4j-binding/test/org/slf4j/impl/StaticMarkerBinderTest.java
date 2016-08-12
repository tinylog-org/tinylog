/*
 * Copyright 2014 Martin Winandy
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.slf4j.helpers.BasicMarkerFactory;

/**
 * Test marker binder.
 *
 * @see StaticMarkerBinder
 */
public class StaticMarkerBinderTest {

	/**
	 * Test returned marker factory.
	 */
	@Test
	public final void testMarkerFactory() {
		assertEquals(BasicMarkerFactory.class.getName(), StaticMarkerBinder.SINGLETON.getMarkerFactoryClassStr());

		BasicMarkerFactory first = StaticMarkerBinder.SINGLETON.getMarkerFactory();
		assertNotNull(first);
		BasicMarkerFactory second = StaticMarkerBinder.SINGLETON.getMarkerFactory();
		assertSame(first, second);
	}

}
