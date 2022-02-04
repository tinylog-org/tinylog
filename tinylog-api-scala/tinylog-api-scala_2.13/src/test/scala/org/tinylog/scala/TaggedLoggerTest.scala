/*
 * Copyright 2022 Martin Winandy
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

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.core.classloader.annotations.PrepareForTest

import java.util
import scala.jdk.CollectionConverters.SetHasAsJava

/**
  * Tests for logging methods of [[org.tinylog.scala.Logger]].
  */
@RunWith(classOf[Parameterized])
@PrepareForTest(Array(classOf[org.tinylog.TaggedLogger]))
final class TaggedLoggerTest(tag1Configuration: LevelConfiguration, tag2Configuration: LevelConfiguration)
extends AbstractTaggedLoggerTest(tag1Configuration, tag2Configuration) {

  override protected def convertIntoJavaSet(set: Set[String]): util.Set[String] = set.asJava

}
