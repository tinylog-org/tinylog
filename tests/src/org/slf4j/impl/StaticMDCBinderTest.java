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

import org.junit.Test;
import org.slf4j.spi.MDCAdapter;

/**
 * Test MDC binder.
 *
 * @see StaticMDCBinder
 */
public class StaticMDCBinderTest {

	/**
	 * Test returned MDC adapter.
	 */
	@Test
	public final void testMDCAdapter() {
		assertNotNull(StaticMDCBinder.SINGLETON);
		assertEquals(TinylogMDCAdapter.class.getName(), StaticMDCBinder.SINGLETON.getMDCAdapterClassStr());
		
		MDCAdapter adapter = StaticMDCBinder.SINGLETON.getMDCA();
		assertNotNull(adapter);
		assertEquals(adapter.getClass(), TinylogMDCAdapter.class);
	}
	
}
