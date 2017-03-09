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
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Test Apache Log4j 1.x.
 */
public final class Log4j implements Framework {

	private static final String NAME = "Log4j 1";
	private static final String NAME_ASYNC = NAME + " with async appender";

	private final boolean location;
	private final boolean async;
	private Logger logger;
	private Appender appender;

	public Log4j(final boolean location, final boolean async) {
		this.location = location;
		this.async = async;
	}

	@Override
	public String getName() {
		return async ? NAME_ASYNC : NAME;
	}

	@Override
	public void init(final File file) throws IOException {
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		appender = createAppender(file);
		logger.addAppender(appender);
		logger.setLevel(Level.INFO);
	}

	@Override
	public void write(final long number) {
		logger.trace("Trace: " + number);
		logger.debug("Debug: " + number);
		logger.info("Info: " + number);
		logger.warn("Warning: " + number);
		logger.error("Error: " + number);
	}

	@Override
	public boolean calculate(final List<Long> primes, final long number) {
		for (Long prime : primes) {
			if (number % prime == 0L) {
				logger.trace(number + " is not prime");
				return false;
			}
		}
		logger.info(number + " is prime");
		return true;
	}

	@Override
	public void dispose() {
		appender.close();
	}

	private Appender createAppender(final File file) throws IOException {
		if (async) {
			AsyncAppender appender = new AsyncAppender();
			appender.setLocationInfo(location);
			appender.addAppender(new FileAppender(createLayout(), file.getAbsolutePath(), false, true, 64 * 1024));
			return appender;
		} else {
			return new FileAppender(createLayout(), file.getAbsolutePath(), false, false, 0);
		}
	}

	private Layout createLayout() {
		if (location) {
			return new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n");
		} else {
			return new PatternLayout("%d{yyyy-MM-dd HH:mm:ss}: %m%n");
		}
	}

}
