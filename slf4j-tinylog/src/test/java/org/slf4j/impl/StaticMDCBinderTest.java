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
import org.tinylog.slf4j.TinylogMdcAdapter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StaticMDCBinder}.
 */
// @checkstyle off: AbbreviationAsWordInName
public final class StaticMDCBinderTest {

	/**
	 * Verifies that {@link TinylogMdcAdapter} is returned as MDC adapter.
	 */
	@Test
	public void instance() {
		assertThat(StaticMDCBinder.SINGLETON.getMDCA()).isInstanceOf(TinylogMdcAdapter.class);
	}

	/**
	 * Verifies that the fully-qualified class name of {@link TinylogMdcAdapter} is returned as MDC adapter class name.
	 */
	@Test
	public void className() {
		assertThat(StaticMDCBinder.SINGLETON.getMDCAdapterClassStr()).isEqualTo(TinylogMdcAdapter.class.getName());
	}

}
