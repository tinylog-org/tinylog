/*
 * Copyright 2019 Martin Winandy
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

package org.apache.log4j;

import java.util.Stack;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NDC}.
 */
public final class NdcTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that NDC can be cleared without any side effects.
	 */
	@Test
	public void clear() {
		NDC.clear();
	}

	/**
	 * Verifies that {@code null} is returned as cloned stack.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void cloneStack() {
		assertThat(NDC.cloneStack()).isNull();
	}

	/**
	 * Verifies that NDC can be inherited without any side effects.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void inherit() {
		NDC.inherit(new Stack());
	}

	/**
	 * Verifies that the current depth is fixed to zero.
	 */
	@Test
	public void getDepth() {
		assertThat(NDC.getDepth()).isZero();
	}

	/**
	 * Verifies that always an empty string is popped.
	 */
	@Test
	public void pop() {
		assertThat(NDC.pop()).isEmpty();
	}

	/**
	 * Verifies that always an empty string is peeked.
	 */
	@Test
	public void peek() {
		assertThat(NDC.peek()).isEmpty();
	}

	/**
	 * Verifies that a pushed message will be discarded and a warning will be output.
	 */
	@Test
	public void push() {
		NDC.push("Hello World!");

		assertThat(NDC.getDepth()).isZero();
		assertThat(NDC.peek()).isEmpty();

		assertThat(systemStream.consumeErrorOutput()).contains("NDC", "Hello World!");
	}

	/**
	 * Verifies that can be removed without any side effects.
	 */
	@Test
	public void remove() {
		NDC.remove();
	}

	/**
	 * Verifies that the maximum depth be set without any side effects.
	 */
	@Test
	public void setMaxDepth() {
		NDC.setMaxDepth(42);
	}

}
