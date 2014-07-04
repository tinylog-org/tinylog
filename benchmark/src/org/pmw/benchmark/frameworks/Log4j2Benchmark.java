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

package org.pmw.benchmark.frameworks;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.async.DaemonThreadFactory;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;
import org.apache.logging.log4j.core.async.RingBufferLogEventHandler;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.status.StatusLogger;
import org.pmw.benchmark.Benchmark;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Test Apache Log4j 2.
 */
public final class Log4j2Benchmark implements Benchmark {

	private static final String NAME = "log4j 2";
	private static final String NAME_ASYNC = NAME + " with async logger";

	private final boolean async;
	private Logger logger;
	private Appender appender;

	public Log4j2Benchmark(final boolean async) {
		this.async = async;
	}

	@Override
	public String getName() {
		return async ? NAME_ASYNC : NAME;
	}

	@Override
	public void init(final File file) {
		if (async) {
			try {
				reInitAsyncLogger();
			} catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		}

		logger = (Logger) LogManager.getLogger();
		Configuration configuration = logger.getContext().getConfiguration();

		for (Appender appender : configuration.getAppenders().values()) {
			logger.removeAppender(appender);
		}
		appender = createAppender(file, configuration);
		appender.start();
		logger.addAppender(appender);

		logger.setLevel(Level.INFO);
	}

	@Override
	public void write(final long number) {
		logger.trace("Trace: {}", number);
		logger.debug("Debug: {}", number);
		logger.info("Info: {}", number);
		logger.warn("Warning: {}", number);
		logger.error("Error: {}", number);
	}

	@Override
	public boolean calculate(final List<Long> primes, final long number) {
		for (Long prime : primes) {
			if (number % prime == 0L) {
				logger.trace("{} is not prime", number);
				return false;
			}
		}
		logger.info("{} is prime", number);
		return true;
	}

	@Override
	public void dispose() {
		if (async) {
			AsyncLogger.stop();
		}

		appender.stop();
	}

	private Layout<? extends Serializable> createLayout(final Configuration configuration) {
		return PatternLayout.createLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n", configuration, null, null, null, null);
	}

	private Appender createAppender(final File file, final Configuration configuration) {
		return FileAppender.createAppender(file.getAbsolutePath(), "false", null, "file", async ? "false" : "true", null, "true", null,
				createLayout(configuration), null, null, null, configuration);
	}

	/* Evil workaround -> must do the same as org.apache.logging.log4j.core.async.AsyncLogger.<clinit>() */
	private static void reInitAsyncLogger() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<AsyncLogger> asyncLoggerClass = AsyncLogger.class;

		Executor executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("AsyncLogger-"));
		Field executorField = asyncLoggerClass.getDeclaredField("executor");
		executorField.setAccessible(true);
		executorField.set(null, executor);

		Method calculateRingBufferSize = asyncLoggerClass.getDeclaredMethod("calculateRingBufferSize");
		calculateRingBufferSize.setAccessible(true);
		int ringBufferSize = (int) calculateRingBufferSize.invoke(null);

		Method createWaitStrategy = asyncLoggerClass.getDeclaredMethod("createWaitStrategy");
		createWaitStrategy.setAccessible(true);
		WaitStrategy waitStrategy = (WaitStrategy) createWaitStrategy.invoke(null);

		Disruptor<RingBufferLogEvent> disruptor = new Disruptor<RingBufferLogEvent>(RingBufferLogEvent.FACTORY, ringBufferSize, executor, ProducerType.MULTI,
				waitStrategy);
		Field disruptorField = asyncLoggerClass.getDeclaredField("disruptor");
		disruptorField.setAccessible(true);
		disruptorField.set(null, disruptor);
		Method getExceptionHandler = asyncLoggerClass.getDeclaredMethod("getExceptionHandler");
		getExceptionHandler.setAccessible(true);
		ExceptionHandler exceptionHandler = (ExceptionHandler) getExceptionHandler.invoke(null);
		disruptor.handleExceptionsWith(exceptionHandler);
		EventHandler<RingBufferLogEvent>[] handlers = new RingBufferLogEventHandler[] { new RingBufferLogEventHandler() };
		disruptor.handleEventsWith(handlers);

		Field statusLoggerField = asyncLoggerClass.getDeclaredField("LOGGER");
		statusLoggerField.setAccessible(true);
		((StatusLogger) statusLoggerField.get(null)).debug("Starting AsyncLogger disruptor with ringbuffer size {}...", disruptor.getRingBuffer()
				.getBufferSize());

		disruptor.start();
	}

}
