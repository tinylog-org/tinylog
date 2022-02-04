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

import org.tinylog.Level

/**
  * This is intended to provide information about which log levels can be logged by a level. For example, TRACE is able to log all
  * levels, whereas ERROR can log only at the ERROR level and OFF means none of the levels may be logged.
  *
  * @param level
  * The actual log level that the information is for
  * @param traceEnabled
  * Determines if [[org.tinylog.Level#TRACE]] is enabled for the [[org.tinylog.Level#level]]
  * @param debugEnabled
  * Determines if [[org.tinylog.Level#DEBUG]] is enabled for the [[org.tinylog.Level#level]]
  * @param infoEnabled
  * Determines if [[org.tinylog.Level#INFO]] is enabled for the [[org.tinylog.Level#level]]
  * @param warnEnabled
  * Determines if [[org.tinylog.Level#WARN]] is enabled for the [[org.tinylog.Level#level]]
  * @param errorEnabled
  * Determines if [[org.tinylog.Level#ERROR]] is enabled for the [[org.tinylog.Level#level]]
  */
final class LevelConfiguration(val level: Level, val traceEnabled: Boolean, val debugEnabled: Boolean,
                               val infoEnabled: Boolean, val warnEnabled: Boolean, val errorEnabled: Boolean) {
  override def toString: String = { level.toString() }
}

object LevelConfiguration {
  /**
    * List of all severity levels each other levels are "enabled" by them. The other level is enabled if it can be logged. This usually
    * means that the other level is also of a higher severity level.
    */
  val AVAILABLE_LEVELS = Array(
    new LevelConfiguration(
      level = Level.TRACE,
      traceEnabled = true,
      debugEnabled = true,
      infoEnabled = true,
      warnEnabled = true,
      errorEnabled = true
    ),
    new LevelConfiguration(
      level = Level.DEBUG,
      traceEnabled = false,
      debugEnabled = true,
      infoEnabled = true,
      warnEnabled = true,
      errorEnabled = true
    ),
    new LevelConfiguration(
      level = Level.INFO,
      traceEnabled = false,
      debugEnabled = false,
      infoEnabled = true,
      warnEnabled = true,
      errorEnabled = true
    ),
    new LevelConfiguration(
      level = Level.WARN,
      traceEnabled = false,
      debugEnabled = false,
      infoEnabled = false,
      warnEnabled = true,
      errorEnabled = true
    ),
    new LevelConfiguration(
      level = Level.ERROR,
      traceEnabled = false,
      debugEnabled = false,
      infoEnabled = false,
      warnEnabled = false,
      errorEnabled = true
    ),
    new LevelConfiguration(
      level = Level.OFF,
      traceEnabled = false,
      debugEnabled = false,
      infoEnabled = false,
      warnEnabled = false,
      errorEnabled = false
    )
  )
}