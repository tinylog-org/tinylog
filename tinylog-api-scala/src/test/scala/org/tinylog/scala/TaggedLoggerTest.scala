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

import java.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.{After, Before, Rule, Test}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.{any, anyInt, anyString, argThat, isNull, eq => eqTo}
import org.mockito.Mockito.{mock, never, verify, when}
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.powermock.reflect.Whitebox
import org.tinylog.provider.{LoggingProvider, ProviderRegistry}
import org.tinylog.rules.SystemStreamCollector
import org.tinylog.{Level, Supplier}

/**
	* Tests for logging methods of [[org.tinylog.scala.Logger]].
	*
	* @param level
	* Actual severity level under test
	* @param traceEnabled
	* Determines if [[org.tinylog.Level#TRACE]] is enabled
	* @param debugEnabled
	* Determines if [[org.tinylog.Level#DEBUG]] is enabled
	* @param infoEnabled
	* Determines if [[org.tinylog.Level#INFO]] is enabled
	* @param warnEnabled
	* Determines if [[org.tinylog.Level#WARN]] is enabled
	* @param errorEnabled
	* Determines if [[org.tinylog.Level#ERROR]] is enabled
	*/
@RunWith(classOf[Parameterized])
@PrepareForTest(Array(classOf[org.tinylog.TaggedLogger]))
final class TaggedLoggerTest(var level: Level, var traceEnabled: Boolean, var debugEnabled: Boolean, var infoEnabled: Boolean, var warnEnabled: Boolean, var errorEnabled: Boolean) {

	private val TAG = "test"

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

	/**
		* Mocks the underlying logging provider and creates a new tagged logger instance.
		*/
	@Before def init(): Unit = {
		logger = new TaggedLogger(TAG)
		loggingProvider = mockLoggingProvider()
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
		assertThat(logger.isTraceEnabled).isEqualTo(traceEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceNumber(): Unit = {
		logger.trace(42.asInstanceOf[Any])

		if (traceEnabled) verify(loggingProvider).log(2, TAG, Level.TRACE, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceStaticString(): Unit = {
		logger.trace("Hello World!")

		if (traceEnabled) verify(loggingProvider).log(2, TAG, Level.TRACE, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceInterpolatedString(): Unit = {
		val name = "Mister"
		logger.trace(s"Hello $name!")

		if (traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.TRACE), isNull[Throwable], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceLazyMessage(): Unit = {
		logger.trace(() => "Hello World!")

		if (traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.TRACE), isNull[Throwable], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceMessageAndArguments(): Unit = {
		logger.trace("Hello {}!", "World")

		if (traceEnabled) verify(loggingProvider).log(2, TAG, Level.TRACE, null, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceMessageAndLazyArguments(): Unit = {
		logger.trace("The number is {}", () => 42)

		if (traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.TRACE), isNull[Throwable], eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceException(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception)

		if (traceEnabled) verify(loggingProvider).log(2, TAG, Level.TRACE, exception, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, "Hello World!")

		if (traceEnabled) verify(loggingProvider).log(2, TAG, Level.TRACE, exception, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.trace(exception, s"Hello $name!")

		if (traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.TRACE), eqTo(exception), argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, () => "Hello World!")

		if (traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.TRACE), eqTo(exception), argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, "Hello {}!", "World")

		if (traceEnabled) verify(loggingProvider).log(2, TAG, Level.TRACE, exception, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.trace(exception, "The number is {}", () => 42)

		if (traceEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.TRACE), eqTo(exception), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#DEBUG]] is enabled.
		*/
	@Test def isDebugEnabled(): Unit = {
		assertThat(logger.isDebugEnabled).isEqualTo(debugEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugNumber(): Unit = {
		logger.debug(42.asInstanceOf[Any])

		if (debugEnabled) verify(loggingProvider).log(2, TAG, Level.DEBUG, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugStaticString(): Unit = {
		logger.debug("Hello World!")

		if (debugEnabled) verify(loggingProvider).log(2, TAG, Level.DEBUG, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugInterpolatedString(): Unit = {
		val name = "Mister"
		logger.debug(s"Hello $name!")

		if (debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.DEBUG), isNull[Throwable], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugLazyMessage(): Unit = {
		logger.debug(() => "Hello World!")

		if (debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.DEBUG), isNull[Throwable], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugMessageAndArguments(): Unit = {
		logger.debug("Hello {}!", "World")

		if (debugEnabled) verify(loggingProvider).log(2, TAG, Level.DEBUG, null, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugMessageAndLazyArguments(): Unit = {
		logger.debug("The number is {}", () => 42)

		if (debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.DEBUG), isNull[Throwable], eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugException(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception)

		if (debugEnabled) verify(loggingProvider).log(2, TAG, Level.DEBUG, exception, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, "Hello World!")

		if (debugEnabled) verify(loggingProvider).log(2, TAG, Level.DEBUG, exception, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.debug(exception, s"Hello $name!")

		if (debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.DEBUG), eqTo(exception), argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, () => "Hello World!")

		if (debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.DEBUG), eqTo(exception), argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, "Hello {}!", "World")

		if (debugEnabled) verify(loggingProvider).log(2, TAG, Level.DEBUG, exception, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.debug(exception, "The number is {}", () => 42)

		if (debugEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.DEBUG), eqTo(exception), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#INFO]] is enabled.
		*/
	@Test def isInfoEnabled(): Unit = {
		assertThat(logger.isInfoEnabled).isEqualTo(infoEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoNumber(): Unit = {
		logger.info(42.asInstanceOf[Any])

		if (infoEnabled) verify(loggingProvider).log(2, TAG, Level.INFO, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoStaticString(): Unit = {
		logger.info("Hello World!")

		if (infoEnabled) verify(loggingProvider).log(2, TAG, Level.INFO, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoInterpolatedString(): Unit = {
		val name = "Mister"
		logger.info(s"Hello $name!")

		if (infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.INFO), isNull[Throwable], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoLazyMessage(): Unit = {
		logger.info(() => "Hello World!")

		if (infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.INFO), isNull[Throwable], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoMessageAndArguments(): Unit = {
		logger.info("Hello {}!", "World")

		if (infoEnabled) verify(loggingProvider).log(2, TAG, Level.INFO, null, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoMessageAndLazyArguments(): Unit = {
		logger.info("The number is {}", () => 42)

		if (infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.INFO), isNull[Throwable], eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoException(): Unit = {
		val exception = new NullPointerException
		logger.info(exception)

		if (infoEnabled) verify(loggingProvider).log(2, TAG, Level.INFO, exception, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, "Hello World!")

		if (infoEnabled) verify(loggingProvider).log(2, TAG, Level.INFO, exception, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.info(exception, s"Hello $name!")

		if (infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.INFO), eqTo(exception), argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, () => "Hello World!")

		if (infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.INFO), eqTo(exception), argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, "Hello {}!", "World")

		if (infoEnabled) verify(loggingProvider).log(2, TAG, Level.INFO, exception, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.info(exception, "The number is {}", () => 42)

		if (infoEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.INFO), eqTo(exception), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#WARN]] is enabled.
		*/
	@Test def isWarnEnabled(): Unit = {
		assertThat(logger.isWarnEnabled).isEqualTo(warnEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnNumber(): Unit = {
		logger.warn(42.asInstanceOf[Any])

		if (warnEnabled) verify(loggingProvider).log(2, TAG, Level.WARN, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnStaticString(): Unit = {
		logger.warn("Hello World!")

		if (warnEnabled) verify(loggingProvider).log(2, TAG, Level.WARN, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnInterpolatedString(): Unit = {
		val name = "Mister"
		logger.warn(s"Hello $name!")

		if (warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.WARN), isNull[Throwable], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnLazyMessage(): Unit = {
		logger.warn(() => "Hello World!")

		if (warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.WARN), isNull[Throwable], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnMessageAndArguments(): Unit = {
		logger.warn("Hello {}!", "World")

		if (warnEnabled) verify(loggingProvider).log(2, TAG, Level.WARN, null, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnMessageAndLazyArguments(): Unit = {
		logger.warn("The number is {}", () => 42)

		if (warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.WARN), isNull[Throwable], eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnException(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception)

		if (warnEnabled) verify(loggingProvider).log(2, TAG, Level.WARN, exception, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, "Hello World!")

		if (warnEnabled) verify(loggingProvider).log(2, TAG, Level.WARN, exception, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.warn(exception, s"Hello $name!")

		if (warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.WARN), eqTo(exception), argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, () => "Hello World!")

		if (warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.WARN), eqTo(exception), argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, "Hello {}!", "World")

		if (warnEnabled) verify(loggingProvider).log(2, TAG, Level.WARN, exception, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.warn(exception, "The number is {}", () => 42)

		if (warnEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.WARN), eqTo(exception), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#ERROR]] is enabled.
		*/
	@Test def isErrorEnabled(): Unit = {
		assertThat(logger.isErrorEnabled).isEqualTo(errorEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorNumber(): Unit = {
		logger.error(42.asInstanceOf[Any])

		if (errorEnabled) verify(loggingProvider).log(2, TAG, Level.ERROR, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorStaticString(): Unit = {
		logger.error("Hello World!")

		if (errorEnabled) verify(loggingProvider).log(2, TAG, Level.ERROR, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorInterpolatedString(): Unit = {
		val name = "Mister"
		logger.error(s"Hello $name!")

		if (errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.ERROR), isNull[Throwable], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorLazyMessage(): Unit = {
		logger.error(() => "Hello World!")

		if (errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.ERROR), isNull[Throwable], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorMessageAndArguments(): Unit = {
		logger.error("Hello {}!", "World")

		if (errorEnabled) verify(loggingProvider).log(2, TAG, Level.ERROR, null, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorMessageAndLazyArguments(): Unit = {
		logger.error("The number is {}", () => 42)

		if (errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.ERROR), isNull[Throwable], eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorException(): Unit = {
		val exception = new NullPointerException
		logger.error(exception)

		if (errorEnabled) verify(loggingProvider).log(2, TAG, Level.ERROR, exception, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, "Hello World!")

		if (errorEnabled) verify(loggingProvider).log(2, TAG, Level.ERROR, exception, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		logger.error(exception, s"Hello $name!")

		if (errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.ERROR), eqTo(exception), argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, () => "Hello World!")

		if (errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.ERROR), eqTo(exception), argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, "Hello {}!", "World")

		if (errorEnabled) verify(loggingProvider).log(2, TAG, Level.ERROR, exception, "Hello {}!", "World")
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		logger.error(exception, "The number is {}", () => 42)

		if (errorEnabled) verify(loggingProvider).log(eqTo(2), eqTo(TAG), eqTo(Level.ERROR), eqTo(exception), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Mocks the logging provider for [[org.tinylog.TaggedLogger]] and overrides all depending fields.
		*
		* @return Mock instance for logging provider
		*/
	private def mockLoggingProvider(): LoggingProvider = {
		val provider = mock(classOf[LoggingProvider])

		when(provider.getMinimumLevel(TAG)).thenReturn(level)
		when(provider.isEnabled(anyInt, eqTo(TAG), eqTo(Level.TRACE))).thenReturn(traceEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG), eqTo(Level.DEBUG))).thenReturn(debugEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG), eqTo(Level.INFO))).thenReturn(infoEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG), eqTo(Level.WARN))).thenReturn(warnEnabled)
		when(provider.isEnabled(anyInt, eqTo(TAG), eqTo(Level.ERROR))).thenReturn(errorEnabled)

		Whitebox.setInternalState(classOf[org.tinylog.TaggedLogger], provider)

		Whitebox.setInternalState(logger.logger, "minimumLevelCoversTrace", traceEnabled)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversDebug", debugEnabled)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversInfo", infoEnabled)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversWarn", warnEnabled)
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversError", errorEnabled)

		return provider
	}

	/**
		* Resets the logging provider and all overridden fields in [[org.tinylog.TaggedLogger]].
		*/
	private def resetLoggingProvider(): Unit = {
		Whitebox.setInternalState(classOf[org.tinylog.TaggedLogger], ProviderRegistry.getLoggingProvider)

		Whitebox.setInternalState(logger.logger, "minimumLevelCoversTrace", isCoveredByMinimumLevel(Level.TRACE))
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversDebug", isCoveredByMinimumLevel(Level.DEBUG))
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversInfo", isCoveredByMinimumLevel(Level.INFO))
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversWarn", isCoveredByMinimumLevel(Level.WARN))
		Whitebox.setInternalState(logger.logger, "minimumLevelCoversError", isCoveredByMinimumLevel(Level.ERROR))
	}
	/**
		* Invokes the private method [[org.tinylog.TaggedLogger#isCoveredByMinimumLevel]].
		*
		* @param level
		* Severity level to check
		* @return `true` if given severity level is covered, otherwise `false`
		*/
	private def isCoveredByMinimumLevel(level: Level): Boolean = {
		return Whitebox.invokeMethod(classOf[org.tinylog.TaggedLogger], "isCoveredByMinimumLevel", TAG, level)
	}

	/**
		* Verifies that a supplier retrieves an expected return value.
		*
		* @param expected
		* Expected return value
		* @return Argument matcher for testing suppliers
		*/
	private def supplies(expected: Any): ArgumentMatcher[Supplier[Any]] = {
		return supplier => {
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
		* Returns for all severity levels which severity levels are enabled.
		*
		* @return Each object array contains the severity level itself and five booleans for [[org.tinylog.Level#TRACE]]
		*         ... [[org.tinylog.Level#ERROR]] to determine whether these severity levels are enabled
		*/
	@Parameters(name = "{0}") def getLevels: util.Collection[Array[AnyRef]] = {
		val levels = new util.ArrayList[Array[AnyRef]]
		// @formatter:off
		levels.add(Array[AnyRef](Level.TRACE, Boolean.box(true),  Boolean.box(true),  Boolean.box(true),  Boolean.box(true),  Boolean.box(true)))
		levels.add(Array[AnyRef](Level.DEBUG, Boolean.box(false), Boolean.box(true),  Boolean.box(true),  Boolean.box(true),  Boolean.box(true)))
		levels.add(Array[AnyRef](Level.INFO,  Boolean.box(false), Boolean.box(false), Boolean.box(true),  Boolean.box(true),  Boolean.box(true)))
		levels.add(Array[AnyRef](Level.WARN,  Boolean.box(false), Boolean.box(false), Boolean.box(false), Boolean.box(true),  Boolean.box(true)))
		levels.add(Array[AnyRef](Level.ERROR, Boolean.box(false), Boolean.box(false), Boolean.box(false), Boolean.box(false), Boolean.box(true)))
		levels.add(Array[AnyRef](Level.OFF,   Boolean.box(false), Boolean.box(false), Boolean.box(false), Boolean.box(false), Boolean.box(false)))
		// @formatter:on
		levels
	}

}
