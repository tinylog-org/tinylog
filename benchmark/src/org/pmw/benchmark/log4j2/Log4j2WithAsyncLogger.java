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

package org.pmw.benchmark.log4j2;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.async.DaemonThreadFactory;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;
import org.apache.logging.log4j.core.async.RingBufferLogEventHandler;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.status.StatusLogger;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class Log4j2WithAsyncLogger extends Log4j2 {

	@Override
	public String getName() {
		return "log4j 2 with async logger";
	}

	@Override
	public void init(final File file) throws Exception {
		reInitAsyncLogger();
		super.init(file);
	}

	@Override
	public void dispose() throws Exception {
		AsyncLogger.stop();
		super.dispose();
	}

	@Override
	protected Appender createAppender(final File file, final Configuration configuration) {
		return FileAppender.createAppender(file.getAbsolutePath(), "false", null, "File", null, null, "true", null, createLayout(configuration), null, null,
				null, configuration);
	}

	/* Evil workaround -> must do the same as org.apache.logging.log4j.core.async.AsyncLogger.<clinit>() */
	private void reInitAsyncLogger() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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
