/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.benchmarks.logging.jul;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Simple formatter for formatting log records.
 */
public final class SimpleFormatter extends Formatter {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private final SimpleDateFormat formatter;

	/**
	 *
	 */
	public SimpleFormatter() {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		builder.append(Thread.currentThread().getName());
		builder.append("] ");
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
