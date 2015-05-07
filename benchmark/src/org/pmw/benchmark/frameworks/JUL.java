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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Test JUL ({@link java.util.logging.Logger}).
 */
public final class JUL implements Framework {

	public static final String NAME = "JUL";

	private final boolean location;
	private Logger logger;
	private FileHandler handler;

	public JUL(final boolean location) {
		this.location = location;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void init(final File file) throws IOException {
		logger = Logger.getAnonymousLogger();
		handler = new FileHandler(file.getAbsolutePath(), false);
		handler.setFormatter(new MyFormatter(location, "yyyy-MM-dd HH:mm:ss"));
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);
	}

	@Override
	public void write(final long number) {
		logger.log(Level.FINE, "Trace: {0}", number);
		logger.log(Level.CONFIG, "Debug: {0}", number);
		logger.log(Level.INFO, "Info: {0}", number);
		logger.log(Level.WARNING, "Warning: {0}", number);
		logger.log(Level.SEVERE, "Error: {0}", number);
	}

	@Override
	public boolean calculate(final List<Long> primes, final long number) {
		for (Long prime : primes) {
			if (number % prime == 0L) {
				logger.log(Level.FINE, "{0} is not prime", number);
				return false;
			}
		}
		logger.log(Level.INFO, "{0} is prime", number);
		return true;
	}

	@Override
	public void dispose() {
		handler.close();
		logger.removeHandler(handler);
	}

	private static class MyFormatter extends Formatter {

		private static final String NEW_LINE = System.getProperty("line.separator");

		private final boolean location;
		private final SimpleDateFormat formatter;

		public MyFormatter(final boolean location, final String datePattern) {
			this.location = location;
			this.formatter = new SimpleDateFormat(datePattern);
		}

		@Override
		public String format(final LogRecord record) {
			String date;
			synchronized (formatter) {
				date = formatter.format(new Date());
			}
			StringBuilder builder = new StringBuilder();
			builder.append(date);
			if (location) {
				builder.append(" [");
				builder.append(Thread.currentThread().getName());
				builder.append("] ");
				builder.append(record.getSourceClassName());
				builder.append(".");
				builder.append(record.getSourceMethodName());
				builder.append("(): ");
			} else {
				builder.append(": ");
			}
			builder.append(formatMessage(record));
			if (record.getThrown() != null) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				printWriter.println();
				record.getThrown().printStackTrace(printWriter);
				printWriter.close();
				builder.append(stringWriter.toString());
			}
			builder.append(NEW_LINE);
			return builder.toString();
		}

	}

}
