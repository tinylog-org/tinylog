/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.benchmark.jdk;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.pmw.benchmark.IBenchmark;

public class NativeLoggerBenchmark implements IBenchmark {

	private Logger logger;
	private FileHandler handler;

	@Override
	public String getName() {
		return "native JDK";
	}

	@Override
	public void log(final int index) {
		logger.log(Level.FINE, "Trace: {0}, PI: {1}", new Object[] { index, Math.PI });
		logger.log(Level.CONFIG, "Debug: {0}, PI: {1}", new Object[] { index, Math.PI });
		logger.log(Level.INFO, "Info: {0}, PI: {1}", new Object[] { index, Math.PI });
		logger.log(Level.WARNING, "Warning: {0}, PI: {1}", new Object[] { index, Math.PI });
		logger.log(Level.SEVERE, "Error: {0}, PI: {1}", new Object[] { index, Math.PI });

	}

	@Override
	public void init(final File file) throws Exception {
		logger = Logger.getAnonymousLogger();
		handler = new FileHandler(file.getAbsolutePath());
		handler.setFormatter(new MyFormatter("yyyy-MM-dd HH:mm:ss"));
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);
	}

	@Override
	public void dispose() throws Exception {
		logger.removeHandler(handler);
	}

	private static class MyFormatter extends Formatter {

		private static final String NEW_LINE = System.getProperty("line.separator");
		private final SimpleDateFormat formatter;

		public MyFormatter(final String datePattern) {
			formatter = new SimpleDateFormat(datePattern);
		}

		@Override
		public String format(final LogRecord record) {
			String date;
			synchronized (formatter) {
				date = formatter.format(new Date());
			}
			return MessageFormat.format("{0} [{1}] {2}.{3}(): {4}{5}", date, Thread.currentThread().getName(), record.getSourceClassName(),
					record.getSourceMethodName(), formatMessage(record), NEW_LINE);
		}

	}

}
