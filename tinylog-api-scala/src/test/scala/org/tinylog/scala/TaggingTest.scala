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

package org.tinylog.scala

import org.assertj.core.api.Assertions.assertThat
import org.junit.{Rule, Test}
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox
import org.tinylog.rules.SystemStreamCollector

/**
	* Tests for receiving tagged logger instances.
	*/
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[TaggedLogger]))
final class TaggingTest {

	/**
		* Redirects and collects system output streams.
		*/
	@Rule val systemStream = new SystemStreamCollector(false)

	/**
		* Verifies that [[org.tinylog.scala.Logger#tag(String)]] returns the same untagged instance of
		* [[org.tinylog.scala.TaggedLogger]] for `null` and empty strings.
		*/
	@Test def untagged(): Unit = {
		val logger = Logger.tag(null)
		assertThat(logger).isNotNull.isSameAs(Logger.tag(""))
		assertThat(Whitebox.getInternalState[String](logger, "tag")).isNull()
	}

	/**
		* Verifies that [[org.tinylog.scala.Logger#tag(String)]] returns the same tagged instance of
		* [[org.tinylog.scala.TaggedLogger]] for each tag.
		*/
	@Test def tagged(): Unit = {
		val logger = Logger.tag("test")
		assertThat(logger).isNotNull.isSameAs(Logger.tag("test")).isNotSameAs(Logger.tag("other"))
		assertThat(Whitebox.getInternalState[String](logger, "tag")).isEqualTo("test")
	}

}
