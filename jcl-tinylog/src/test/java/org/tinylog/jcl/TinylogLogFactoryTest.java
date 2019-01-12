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

package org.tinylog.jcl;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TinylogLogFactory}.
 */
public final class TinylogLogFactoryTest {

	private TinylogLogFactory factory;

	/**
	 * Creates the log factory.
	 */
	@Before
	public void init() {
		factory = new TinylogLogFactory();
	}

	/**
	 * Verifies that always the same {@link TinylogLog} instance will be returned.
	 */
	@Test
	public void cachedLogInstance() {
		Log log = factory.getInstance("com.example.MyClass");
		assertThat(log).isInstanceOf(TinylogLog.class);

		assertThat(factory.getInstance("com.example.OtherClass")).isSameAs(log);
		assertThat(factory.getInstance(TinylogLogFactoryTest.class)).isSameAs(log);
	}

	/**
	 * Verifies that new attributes can be defined.
	 */
	@Test
	public void setAttributes() {
		factory.setAttribute("a", 42);
		factory.setAttribute("b", Math.PI);
		factory.setAttribute("c", null);

		assertThat(factory.getAttributeNames()).containsExactlyInAnyOrder("a", "b");

		assertThat(factory.getAttribute("a")).isEqualTo(42);
		assertThat(factory.getAttribute("b")).isEqualTo(Math.PI);
		assertThat(factory.getAttribute("c")).isNull();
	}

	/**
	 * Verifies that existing attributes can be overwritten.
	 */
	@Test
	public void overwriteAttributes() {
		factory.setAttribute("a", 1);
		factory.setAttribute("b", 2);
		factory.setAttribute("c", 3);

		factory.setAttribute("b", null);
		factory.setAttribute("c", 42);

		assertThat(factory.getAttributeNames()).containsExactlyInAnyOrder("a", "c");

		assertThat(factory.getAttribute("a")).isEqualTo(1);
		assertThat(factory.getAttribute("b")).isNull();
		assertThat(factory.getAttribute("c")).isEqualTo(42);
	}

	/**
	 * Verifies that existing attributes can be removed.
	 */
	@Test
	public void removeAttributes() {
		factory.setAttribute("a", 1);
		factory.setAttribute("b", 2);

		factory.removeAttribute("a");
		factory.removeAttribute("c");

		assertThat(factory.getAttributeNames()).containsExactlyInAnyOrder("b");
		assertThat(factory.getAttribute("b")).isEqualTo(2);
	}

	/**
	 * Verifies that {@link TinylogLogFactory#release()} is callable and throws no exceptions.
	 */
	@Test
	public void releasable() {
		factory.release();
	}

}
