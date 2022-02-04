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
import org.junit.{After, Before, Rule, Test}
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.{any, anyInt, anyString, argThat, isNull, eq => eqTo, same}
import org.mockito.Mockito.{mock, never, verify, when}
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.powermock.reflect.Whitebox
import org.tinylog.format.MessageFormatter
import org.tinylog.provider.{LoggingProvider, ProviderRegistry}
import org.tinylog.rules.SystemStreamCollector
import org.tinylog.{Level, Supplier}

/**
	* Tests for logging methods of [[org.tinylog.scala.Logger]].
	*
	* @param level
	* The level and related information about it under test
	*/
@RunWith(classOf[Parameterized])
@PrepareForTest(Array(classOf[org.tinylog.Logger]))
final class LoggerTest(var level: LevelConfiguration) {

	/**
		* Activates PowerMock (alternative to [[org.powermock.modules.junit4.PowerMockRunner]]).
		*/
	@Rule def rule = new PowerMockRule

	/**
		* Redirects and collects system output streams.
		*/
	@Rule def systemStream = new SystemStreamCollector(false)

	private var loggingProvider: LoggingProvider = null

	/**
		* Mocks the underlying logging provider.
		*/
	@Before def init(): Unit = {
		loggingProvider = mockLoggingProvider()
	}

	/**
		* Resets the underlying logging provider.
		*/
	@After def reset(): Unit = {
		resetLoggingProvider()
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#TRACE]] is enabled.
		*/
	@Test def isTraceEnabled(): Unit = {
		assertThat(Logger.isTraceEnabled).isEqualTo(level.traceEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceNumber(): Unit = {
		Logger.trace(42.asInstanceOf[Any])

		if (level.traceEnabled) verify(loggingProvider).log(2, null, Level.TRACE, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceStaticString(): Unit = {
		Logger.trace("Hello World!")

		if (level.traceEnabled) verify(loggingProvider).log(2, null, Level.TRACE, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceInterpolatedString(): Unit = {
		val name = "Mister"
		Logger.trace(s"Hello $name!")

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceLazyMessage(): Unit = {
		Logger.trace(() => "Hello World!")

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceMessageAndArguments(): Unit = {
		Logger.trace("Hello {}!", "World")

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceMessageAndLazyArguments(): Unit = {
		Logger.trace("The number is {}", () => 42)

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceException(): Unit = {
		val exception = new NullPointerException
		Logger.trace(exception)

		if (level.traceEnabled) verify(loggingProvider).log(2, null, Level.TRACE, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		Logger.trace(exception, "Hello World!")

		if (level.traceEnabled) verify(loggingProvider).log(2, null, Level.TRACE, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		Logger.trace(exception, s"Hello $name!")

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		Logger.trace(exception, () => "Hello World!")

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		Logger.trace(exception, "Hello {}!", "World")

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#TRACE]].
		*/
	@Test def traceExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		Logger.trace(exception, "The number is {}", () => 42)

		if (level.traceEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.TRACE), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#DEBUG]] is enabled.
		*/
	@Test def isDebugEnabled(): Unit = {
		assertThat(Logger.isDebugEnabled).isEqualTo(level.debugEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugNumber(): Unit = {
		Logger.debug(42.asInstanceOf[Any])

		if (level.debugEnabled) verify(loggingProvider).log(2, null, Level.DEBUG, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugStaticString(): Unit = {
		Logger.debug("Hello World!")

		if (level.debugEnabled) verify(loggingProvider).log(2, null, Level.DEBUG, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugInterpolatedString(): Unit = {
		val name = "Mister"
		Logger.debug(s"Hello $name!")

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugLazyMessage(): Unit = {
		Logger.debug(() => "Hello World!")

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugMessageAndArguments(): Unit = {
		Logger.debug("Hello {}!", "World")

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugMessageAndLazyArguments(): Unit = {
		Logger.debug("The number is {}", () => 42)

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugException(): Unit = {
		val exception = new NullPointerException
		Logger.debug(exception)

		if (level.debugEnabled) verify(loggingProvider).log(2, null, Level.DEBUG, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		Logger.debug(exception, "Hello World!")

		if (level.debugEnabled) verify(loggingProvider).log(2, null, Level.DEBUG, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		Logger.debug(exception, s"Hello $name!")

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		Logger.debug(exception, () => "Hello World!")

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		Logger.debug(exception, "Hello {}!", "World")

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#DEBUG]].
		*/
	@Test def debugExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		Logger.debug(exception, "The number is {}", () => 42)

		if (level.debugEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.DEBUG), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#INFO]] is enabled.
		*/
	@Test def isInfoEnabled(): Unit = {
		assertThat(Logger.isInfoEnabled).isEqualTo(level.infoEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoNumber(): Unit = {
		Logger.info(42.asInstanceOf[Any])

		if (level.infoEnabled) verify(loggingProvider).log(2, null, Level.INFO, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoStaticString(): Unit = {
		Logger.info("Hello World!")

		if (level.infoEnabled) verify(loggingProvider).log(2, null, Level.INFO, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoInterpolatedString(): Unit = {
		val name = "Mister"
		Logger.info(s"Hello $name!")

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoLazyMessage(): Unit = {
		Logger.info(() => "Hello World!")

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoMessageAndArguments(): Unit = {
		Logger.info("Hello {}!", "World")

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoMessageAndLazyArguments(): Unit = {
		Logger.info("The number is {}", () => 42)

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoException(): Unit = {
		val exception = new NullPointerException
		Logger.info(exception)

		if (level.infoEnabled) verify(loggingProvider).log(2, null, Level.INFO, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		Logger.info(exception, "Hello World!")

		if (level.infoEnabled) verify(loggingProvider).log(2, null, Level.INFO, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		Logger.info(exception, s"Hello $name!")

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		Logger.info(exception, () => "Hello World!")

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		Logger.info(exception, "Hello {}!", "World")

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#INFO]].
		*/
	@Test def infoExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		Logger.info(exception, "The number is {}", () => 42)

		if (level.infoEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.INFO), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#WARN]] is enabled.
		*/
	@Test def isWarnEnabled(): Unit = {
		assertThat(Logger.isWarnEnabled).isEqualTo(level.warnEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnNumber(): Unit = {
		Logger.warn(42.asInstanceOf[Any])

		if (level.warnEnabled) verify(loggingProvider).log(2, null, Level.WARN, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnStaticString(): Unit = {
		Logger.warn("Hello World!")

		if (level.warnEnabled) verify(loggingProvider).log(2, null, Level.WARN, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnInterpolatedString(): Unit = {
		val name = "Mister"
		Logger.warn(s"Hello $name!")

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnLazyMessage(): Unit = {
		Logger.warn(() => "Hello World!")

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnMessageAndArguments(): Unit = {
		Logger.warn("Hello {}!", "World")

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnMessageAndLazyArguments(): Unit = {
		Logger.warn("The number is {}", () => 42)

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnException(): Unit = {
		val exception = new NullPointerException
		Logger.warn(exception)

		if (level.warnEnabled) verify(loggingProvider).log(2, null, Level.WARN, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		Logger.warn(exception, "Hello World!")

		if (level.warnEnabled) verify(loggingProvider).log(2, null, Level.WARN, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		Logger.warn(exception, s"Hello $name!")

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		Logger.warn(exception, () => "Hello World!")

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		Logger.warn(exception, "Hello {}!", "World")

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#WARN]].
		*/
	@Test def warnExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		Logger.warn(exception, "The number is {}", () => 42)

		if (level.warnEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.WARN), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies evaluating whether [[org.tinylog.Level#ERROR]] is enabled.
		*/
	@Test def isErrorEnabled(): Unit = {
		assertThat(Logger.isErrorEnabled).isEqualTo(level.errorEnabled)
	}

	/**
		* Verifies that a number will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorNumber(): Unit = {
		Logger.error(42.asInstanceOf[Any])

		if (level.errorEnabled) verify(loggingProvider).log(2, null, Level.ERROR, null, null, 42, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a static string will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorStaticString(): Unit = {
		Logger.error("Hello World!")

		if (level.errorEnabled) verify(loggingProvider).log(2, null, Level.ERROR, null, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorInterpolatedString(): Unit = {
		val name = "Mister"
		Logger.error(s"Hello $name!")

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a lazy message supplier will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorLazyMessage(): Unit = {
		Logger.error(() => "Hello World!")

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), isNull[Throwable], isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorMessageAndArguments(): Unit = {
		Logger.error("Hello {}!", "World")

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorMessageAndLazyArguments(): Unit = {
		Logger.error("The number is {}", () => 42)

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), isNull[Throwable], any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorException(): Unit = {
		val exception = new NullPointerException
		Logger.error(exception)

		if (level.errorEnabled) verify(loggingProvider).log(2, null, Level.ERROR, exception, null, null, null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a static string will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithStaticString(): Unit = {
		val exception = new NullPointerException
		Logger.error(exception, "Hello World!")

		if (level.errorEnabled) verify(loggingProvider).log(2, null, Level.ERROR, exception, null, "Hello World!", null.asInstanceOf[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with an interpolated string with embedded variables will be logged as lazy supplier at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithInterpolatedString(): Unit = {
		val exception = new NullPointerException
		val name = "Mister"
		Logger.error(exception, s"Hello $name!")

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello Mister!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a lazy message supplier will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithLazyMessage(): Unit = {
		val exception = new NullPointerException
		Logger.error(exception, () => "Hello World!")

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), eqTo(exception), isNull[MessageFormatter], argThat(supplies("Hello World!")), isNull[Array[AnyRef]])
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithMessageAndArguments(): Unit = {
		val exception = new NullPointerException
		Logger.error(exception, "Hello {}!", "World")

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), same(exception), any(classOf[MessageFormatter]), eqTo("Hello {}!"), eqTo("World"))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Verifies that an exception with a formatted text message with lazy argument suppliers will be logged correctly at [[org.tinylog.Level#ERROR]].
		*/
	@Test def errorExceptionWithMessageAndLazyArguments(): Unit = {
		val exception = new NullPointerException
		Logger.error(exception, "The number is {}", () => 42)

		if (level.errorEnabled) verify(loggingProvider).log(eqTo(2), isNull[String], eqTo(Level.ERROR), eqTo(exception), any(classOf[MessageFormatter]), eqTo("The number is {}"), argThat(supplies(42)))
		else verify(loggingProvider, never).log(anyInt, anyString, any, any, any, any, any[Array[AnyRef]])
	}

	/**
		* Mocks the logging provider for [[org.tinylog.Logger]] and overrides all depending fields.
		*
		* @return Mock instance for logging provider
		*/
	private def mockLoggingProvider(): LoggingProvider = {
		val provider = mock(classOf[LoggingProvider])

		when(provider.getMinimumLevel(null)).thenReturn(level.level)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.TRACE))).thenReturn(level.traceEnabled)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.DEBUG))).thenReturn(level.debugEnabled)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.INFO))).thenReturn(level.infoEnabled)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.WARN))).thenReturn(level.warnEnabled)
		when(provider.isEnabled(anyInt, isNull[String], eqTo(Level.ERROR))).thenReturn(level.errorEnabled)

		Whitebox.setInternalState(classOf[org.tinylog.Logger], provider)
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_TRACE", level.traceEnabled)
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_DEBUG", level.debugEnabled)
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_INFO", level.infoEnabled)
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_WARN", level.warnEnabled)
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_ERROR", level.errorEnabled)

		return provider
	}

	/**
		* Resets the logging provider and all overridden fields in [[org.tinylog.Logger]].
		*/
	private def resetLoggingProvider(): Unit = {
		Whitebox.setInternalState(classOf[org.tinylog.Logger], ProviderRegistry.getLoggingProvider)
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_TRACE", isCoveredByMinimumLevel(Level.TRACE))
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_DEBUG", isCoveredByMinimumLevel(Level.DEBUG))
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_INFO", isCoveredByMinimumLevel(Level.INFO))
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_WARN", isCoveredByMinimumLevel(Level.WARN))
		Whitebox.setInternalState(classOf[org.tinylog.Logger], "MINIMUM_LEVEL_COVERS_ERROR", isCoveredByMinimumLevel(Level.ERROR))
	}

	/**
		* Invokes the private method [[org.tinylog.Logger#isCoveredByMinimumLevel]].
		*
		* @param level
		* Severity level to check
		* @return `true` if given severity level is covered, otherwise `false`
		*/
	private def isCoveredByMinimumLevel(level: Level): Boolean = {
		Whitebox.invokeMethod(classOf[org.tinylog.Logger], "isCoveredByMinimumLevel", level)
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
object LoggerTest {

	/**
		* Returns for all severity levels which severity levels are enabled.
		*
		* @return Each object array contains the severity level itself and five booleans for [[org.tinylog.Level#TRACE]]
		*         ... [[org.tinylog.Level#ERROR]] to determine whether these severity levels are enabled
		*/
	@Parameters(name = "{0}") def getLevels: util.Collection[Array[AnyRef]] = {
		val levels = new util.ArrayList[Array[AnyRef]]

		for (level <- LevelConfiguration.AVAILABLE_LEVELS) {
			levels.add(Array[AnyRef](level))
		}

		levels
	}

}
