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
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.{After, Before, Rule, Test}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.{any, anyInt, argThat, isNull, same, eq => eqTo}
import org.mockito.Mockito.{mock, never, verify, when}
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.powermock.reflect.Whitebox
import org.tinylog.format.MessageFormatter
import org.tinylog.provider.LoggingProvider
import org.tinylog.rules.SystemStreamCollector
import org.tinylog.{Level, Supplier}

import java.util
import scala.collection.JavaConverters

/**
	* Tests for logging methods of [[org.tinylog.scala.Logger]].
	*/
@RunWith(classOf[Parameterized])
@PrepareForTest(Array(classOf[org.tinylog.TaggedLogger]))
final class TaggedLoggerTest(val tag1Configuration: LevelConfiguration, val tag2Configuration: LevelConfiguration) {

	private val TAG1 = "test"

	private val TAG2 = "other tag"

	/**
		* Activates PowerMock (alternative to [[org.powermock.modules.junit4.PowerMockRunner]]).
		*/
	@Rule def rule = new PowerMockRule

	/**
		* Redirects and collects system output streams.
		*/
	@Rule def systemStream = new SystemStreamCollector(false)

	private var loggingProvider: LoggingProvider = null
	private var logger: TaggedLogger = null
	private var tags: Set[String] = null

	/**
		* Mocks the underlying logging provider and creates a new tagged logger instance.
		*/
	@Before def init(): Unit = {
    loggingProvider = mockLoggingProvider()
		tags = Set(TAG1)
		if (tag2Configuration != null) {
			tags = tags + TAG2
		}
		logger = new TaggedLogger(tags)
	}

	/**
		* Resets the underlying logging provider.
		*/
	@After
	def reset(): Unit = {
		resetLoggingProvider()
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#TRACE]] is enabled.
		*/
	@Test def isTraceEnabled(): Unit = {
		if (tag2Configuration != null) {
			assertThat(logger.isTraceEnabled).isEqualTo(tag1Configuration.traceEnabled || tag2Configuration.traceEnabled)
		} else {
			assertThat(logger.isTraceEnabled).isEqualTo(tag1Configuration.traceEnabled)
		}
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceNumber(): Unit = {
		logger.trace(42.asInstanceOf[Any])

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(2, TAG1, Level.TRACE, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(2, TAG2, Level.TRACE, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceStaticString(): Unit = {
		logger.trace("Hello World!")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(2, TAG1, Level.TRACE, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(2, TAG2, Level.TRACE, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceInterpolatedString(): Unit = {
		val name = "Mister"
		logger.trace(s"Hello $name!")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceLazyMessage(): Unit = {
		logger.trace(() => "Hello World!")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceMessageAndArguments(): Unit = {
		logger.trace("Hello {}!", "World")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceMessageAndLazyArguments(): Unit = {
		logger.trace("The number is {}", () => 42)

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceException(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception)

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(2, TAG1, Level.TRACE, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(2, TAG2, Level.TRACE, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, "Hello World!")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(2, TAG1, Level.TRACE, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(2, TAG2, Level.TRACE, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.trace(exception, s"Hello $name!")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, () => "Hello World!")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, "Hello {}!", "World")

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, "The number is {}", () => 42)

		if (tag1Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.TRACE), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.TRACE), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.TRACE), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#DEBUG]] is enabled.
		*/
	@Test def isDebugEnabled(): Unit = {
		if (tag2Configuration != null) {
			assertThat(logger.isDebugEnabled)isEqualTo(tag1Configuration.debugEnabled || tag2Configuration.debugEnabled)
		} else {
			assertThat(logger.isDebugEnabled).isEqualTo(tag1Configuration.debugEnabled)
		}
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugNumber(): Unit = {
		logger.debug(42.asInstanceOf[Any])

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(2, TAG1, Level.DEBUG, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(2, TAG2, Level.DEBUG, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugStaticString(): Unit = {
		logger.debug("Hello World!")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(2, TAG1, Level.DEBUG, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(2, TAG2, Level.DEBUG, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugInterpolatedString(): Unit = {
		val name = "Mister"
		logger.debug(s"Hello $name!")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugLazyMessage(): Unit = {
		logger.debug(() => "Hello World!")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugMessageAndArguments(): Unit = {
		logger.debug("Hello {}!", "World")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugMessageAndLazyArguments(): Unit = {
		logger.debug("The number is {}", () => 42)

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugException(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception)

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(2, TAG1, Level.DEBUG, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(2, TAG2, Level.DEBUG, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, "Hello World!")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(2, TAG1, Level.DEBUG, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(2, TAG2, Level.DEBUG, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.debug(exception, s"Hello $name!")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, () => "Hello World!")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, "Hello {}!", "World")

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, "The number is {}", () => 42)

		if (tag1Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.DEBUG), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.DEBUG), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.DEBUG), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#INFO]] is enabled.
		*/
	@Test def isInfoEnabled(): Unit = {
		if (tag2Configuration != null) {
			assertThat(logger.isInfoEnabled).isEqualTo(tag1Configuration.infoEnabled || tag2Configuration.infoEnabled)
		} else {
			assertThat(logger.isInfoEnabled).isEqualTo(tag1Configuration.infoEnabled)
		}
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoNumber(): Unit = {
		logger.info(42.asInstanceOf[Any])

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(2, TAG1, Level.INFO, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(2, TAG2, Level.INFO, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoStaticString(): Unit = {
		logger.info("Hello World!")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(2, TAG1, Level.INFO, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(2, TAG2, Level.INFO, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoInterpolatedString(): Unit = {
		val name = "Mister"
		logger.info(s"Hello $name!")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoLazyMessage(): Unit = {
		logger.info(() => "Hello World!")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoMessageAndArguments(): Unit = {
		logger.info("Hello {}!", "World")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoMessageAndLazyArguments(): Unit = {
		logger.info("The number is {}", () => 42)

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoException(): Unit = {
		val exception = new NullPointerException
		logger.info(exception)

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(2, TAG1, Level.INFO, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(2, TAG2, Level.INFO, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, "Hello World!")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(2, TAG1, Level.INFO, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(2, TAG2, Level.INFO, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.info(exception, s"Hello $name!")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, () => "Hello World!")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, "Hello {}!", "World")

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, "The number is {}", () => 42)

		if (tag1Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.INFO), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.INFO), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.INFO), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#WARN]] is enabled.
		*/
	@Test def isWarnEnabled(): Unit = {
		if (tag2Configuration != null) {
			assertThat(logger.isWarnEnabled).isEqualTo(tag1Configuration.warnEnabled || tag2Configuration.warnEnabled)
		} else {
			assertThat(logger.isWarnEnabled).isEqualTo(tag1Configuration.warnEnabled)
		}
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnNumber(): Unit = {
		logger.warn(42.asInstanceOf[Any])

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(2, TAG1, Level.WARN, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(2, TAG2, Level.WARN, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnStaticString(): Unit = {
		logger.warn("Hello World!")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(2, TAG1, Level.WARN, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(2, TAG2, Level.WARN, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnInterpolatedString(): Unit = {
		val name = "Mister"
		logger.warn(s"Hello $name!")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnLazyMessage(): Unit = {
		logger.warn(() => "Hello World!")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnMessageAndArguments(): Unit = {
		logger.warn("Hello {}!", "World")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), any, any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnMessageAndLazyArguments(): Unit = {
		logger.warn("The number is {}", () => 42)

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnException(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception)

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(2, TAG1, Level.WARN, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(2, TAG2, Level.WARN, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, "Hello World!")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(2, TAG1, Level.WARN, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(2, TAG2, Level.WARN, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.warn(exception, s"Hello $name!")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, () => "Hello World!")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, "Hello {}!", "World")

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, "The number is {}", () => 42)

		if (tag1Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.WARN), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.WARN), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.WARN), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#ERROR]] is enabled.
		*/
	@Test def isErrorEnabled(): Unit = {
		if (tag2Configuration != null) {
		  assertThat(logger.isErrorEnabled).isEqualTo(tag1Configuration.errorEnabled || tag2Configuration.errorEnabled)
		} else {
			assertThat(logger.isErrorEnabled).isEqualTo(tag1Configuration.errorEnabled)
		}
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorNumber(): Unit = {
		logger.error(42.asInstanceOf[Any])

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(2, TAG1, Level.ERROR, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(2, TAG2, Level.ERROR, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorStaticString(): Unit = {
		logger.error("Hello World!")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(2, TAG1, Level.ERROR, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(2, TAG2, Level.ERROR, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorInterpolatedString(): Unit = {
		val name = "Mister"
		logger.error(s"Hello $name!")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorLazyMessage(): Unit = {
		logger.error(() => "Hello World!")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorMessageAndArguments(): Unit = {
		logger.error("Hello {}!", "World")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorMessageAndLazyArguments(): Unit = {
		logger.error("The number is {}", () => 42)

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorException(): Unit = {
		val exception = new NullPointerException
		logger.error(exception)

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(2, TAG1, Level.ERROR, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(2, TAG2, Level.ERROR, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, "Hello World!")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(2, TAG1, Level.ERROR, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(2, TAG2, Level.ERROR, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.error(exception, s"Hello $name!")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, () => "Hello World!")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, "Hello {}!", "World")

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, "The number is {}", () => 42)

		if (tag1Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG1), eqTo(Level.ERROR), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG1), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])

		if (tag2Configuration != null && tag2Configuration.errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG2), eqTo(Level.ERROR), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, eqTo(TAG2), eqTo(Level.ERROR), any, any, any, any[Array[AnyRef]])
	}

	/**
		* Mocks the logging provider for [[org.tinylog.TaggedLogger]] and overrides all depending fields.
		*
		* @return Mock instance for logging provider
		*/
	private def mockLoggingProvider(): LoggingProvider = {
		val provider = mock(classOf[LoggingProvider])

		when(provider.getMinimumLevel(null)).thenReturn(Level.OFF)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.TRACE))).thenReturn(false)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.DEBUG))).thenReturn(false)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.INFO))).thenReturn(false)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.WARN))).thenReturn(false)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.ERROR))).thenReturn(false)

		when(provider.getMinimumLevel(TAG1)).thenReturn(tag1Configuration.level)
		when(provider.isEnabled(anyInt, eqTo(TAG1), eqTo(Level.TRACE))).thenReturn(tag1Configuration.traceEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG1), eqTo(Level.DEBUG))).thenReturn(tag1Configuration.debugEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG1), eqTo(Level.INFO))).thenReturn(tag1Configuration.infoEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG1), eqTo(Level.WARN))).thenReturn(tag1Configuration.warnEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG1), eqTo(Level.ERROR))).thenReturn(tag1Configuration.errorEnabled)

		if (tag2Configuration != null) {
			when(provider.getMinimumLevel(TAG2)).thenReturn(tag2Configuration.level)
			when(provider.isEnabled(anyInt, eqTo(TAG2), eqTo(Level.TRACE))).thenReturn(tag2Configuration.traceEnabled)
			when(provider.isEnabled(anyInt, eqTo(TAG2), eqTo(Level.DEBUG))).thenReturn(tag2Configuration.debugEnabled)
			when(provider.isEnabled(anyInt, eqTo(TAG2), eqTo(Level.INFO))).thenReturn(tag2Configuration.infoEnabled)
			when(provider.isEnabled(anyInt, eqTo(TAG2), eqTo(Level.WARN))).thenReturn(tag2Configuration.warnEnabled)
			when(provider.isEnabled(anyInt, eqTo(TAG2), eqTo(Level.ERROR))).thenReturn(tag2Configuration.errorEnabled)
		}

		Whitebox.setInternalState(classOf[org.tinylog.TaggedLogger], provider)
		return provider
	}

	/**
		* Resets the logging provider and all overridden fields in [[org.tinylog.TaggedLogger]].
		*/
	private def resetLoggingProvider(): Unit = {
		Whitebox.getInternalState[java.util.Map[Set[String], TaggedLogger]](classOf[org.tinylog.Logger], "loggers").clear()

		val traceTags = getCoveredTags(Level.TRACE)
		val debugTags = getCoveredTags(Level.DEBUG)
		val infoTags = getCoveredTags(Level.INFO)
		val warnTags = getCoveredTags(Level.WARN)
		val errorTags = getCoveredTags(Level.ERROR)
		
		Whitebox.setInternalState(logger.logger,  "traceTags": String, traceTags: Any)
		Whitebox.setInternalState(logger.logger, "debugTags": String, debugTags: Any)
		Whitebox.setInternalState(logger.logger, "infoTags": String, infoTags: Any)
		Whitebox.setInternalState(logger.logger, "warnTags": String, warnTags: Any)
		Whitebox.setInternalState(logger.logger, "errorTags": String, errorTags: Any)

		Whitebox.setInternalState(logger.logger, "minimumLevelCoversTrace", !traceTags.isEmpty)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversDebug", !debugTags.isEmpty)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversInfo", !infoTags.isEmpty)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversWarn", !warnTags.isEmpty)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversError", !errorTags.isEmpty)
	}

	/**
		* Invokes the private method [[org.tinylog.TaggedLogger#getCoveredTags]].
		*
		* @param level
		* Severity level to check
		* @return `true` if given severity level is covered, otherwise `false`
		*/
	private def getCoveredTags(level: Level): java.util.Set[String] = {
		Whitebox.invokeMethod(classOf[org.tinylog.TaggedLogger], "getCoveredTags", JavaConverters.setAsJavaSet(tags), level)
	}

	/**
		* Verifies that a supplier retrieves an expected return value.
		*
		* @param expected
		* Expected return value
		* @return Argument matcher for testing suppliers
		*/
	private def supplies(expected: Any): ArgumentMatcher[Supplier[Any]] = {
		supplier => {
			assertThat(supplier.get()).isEqualTo(expected)
			true
		}
	}

}

/**
	* Parameters for testing logging.
	*/
object TaggedLoggerTest {

	/**
		* Returns all different combinations of logging levels for up two tags for the tests.
		*
		* @return Each object array represents a combination. A value of null means the tag isn't present in the combination.
		*/
	@Parameters(name = "{0}, {1}") def getLevels: util.Collection[Array[AnyRef]] = {
		val levels = new util.ArrayList[Array[AnyRef]]

		for (i <- LevelConfiguration.AVAILABLE_LEVELS) {
      levels.add(Array[AnyRef](i, null))
			for (j <- LevelConfiguration.AVAILABLE_LEVELS) {
				levels.add(Array[AnyRef](i, j))
			}
    }
		levels
	}

}
