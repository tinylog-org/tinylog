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

package org.pmw.benchmark.logback;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

public class LogbackWithAsyncAppender extends Logback {

	@Override
	public String getName() {
		return "logback with async appender";
	}

	@Override
	public void dispose() throws Exception {
		super.dispose();
		((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
	}

	@Override
	protected Appender<ILoggingEvent> createAppender(final File file, final LoggerContext context) {
		AsyncAppender appender = new AsyncAppender();
		appender.setIncludeCallerData(true);
		appender.setDiscardingThreshold(0);
		appender.setContext(context);

		Appender<ILoggingEvent> subAppender = super.createAppender(file, context);
		subAppender.start();
		appender.addAppender(subAppender);

		return appender;
	}

	@Override
	protected LayoutWrappingEncoder<ILoggingEvent> createEncoder(final LoggerContext context) {
		LayoutWrappingEncoder<ILoggingEvent> encoder = super.createEncoder(context);
		encoder.setImmediateFlush(false);
		return encoder;
	}

}
