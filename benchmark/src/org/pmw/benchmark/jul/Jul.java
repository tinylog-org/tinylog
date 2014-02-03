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

package org.pmw.benchmark.jul;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.pmw.benchmark.ILoggingFramework;

public class Jul implements ILoggingFramework {

	private Logger logger;
	private FileHandler handler;

	@Override
	public String getName() {
		return "JUL";
	}

	@Override
	public void init(final File file) throws Exception {
		logger = Logger.getAnonymousLogger();
		handler = new FileHandler(file.getAbsolutePath(), false);
		handler.setFormatter(new MyFormatter("yyyy-MM-dd HH:mm:ss"));
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);
	}

	@Override
	public void trace(final Object obj) {
		logger.log(Level.FINE, "Trace: {0}", obj);
	}

	@Override
	public void debug(final Object obj) {
		logger.log(Level.CONFIG, "Debug: {0}", obj);
	}

	@Override
	public void info(final Object obj) {
		logger.log(Level.INFO, "Info: {0}", obj);
	}

	@Override
	public void warning(final Object obj) {
		logger.log(Level.WARNING, "Warning: {0}", obj);
	}

	@Override
	public void error(final Object obj) {
		logger.log(Level.SEVERE, "Error: {0}", obj);
	}

	@Override
	public void dispose() throws Exception {
		handler.close();
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
			StringBuilder builder = new StringBuilder();
			builder.append(date);
			builder.append(" [");
			builder.append(record.getLevel().getName());
			builder.append("] ");
			builder.append(Thread.currentThread().getName());
			builder.append(" ");
			builder.append(record.getSourceClassName());
			builder.append(".");
			builder.append(record.getSourceMethodName());
			builder.append("(): ");
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
