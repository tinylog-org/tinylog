/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.throwable.ThrowableData;
import org.tinylog.throwable.ThrowableFilter;
import org.tinylog.throwable.ThrowableWrapper;

/**
 * Token for outputting the exception or throwable of a log entry.
 */
final class ExceptionToken implements Token {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private final List<ThrowableFilter> filters;

	/**
	 * @param filters
	 *            Throwable filters for output of exceptions and other throwables
	 */
	ExceptionToken(final List<ThrowableFilter> filters) {
		this.filters = filters;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singleton(LogEntryValue.EXCEPTION);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		Throwable throwable = logEntry.getException();
		if (throwable != null) {
			render(filter(throwable), Collections.<StackTraceElement>emptyList(), builder);
		}
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		Throwable throwable = logEntry.getException();
		if (throwable == null) {
			statement.setString(index, null);
		} else {
			StringBuilder builder = new StringBuilder();
			render(filter(throwable), Collections.<StackTraceElement>emptyList(), builder);
			statement.setString(index, builder.toString());
		}
	}

	/**
	 * Applies all registered {@link ThrowableFilter throwable filters}.
	 *
	 * @param throwable
	 *            Throwable to filter
	 * @return Transformed throwable
	 */
	private ThrowableData filter(final Throwable throwable) {
		ThrowableData data = new ThrowableWrapper(throwable);
		for (ThrowableFilter filter : filters) {
			data = filter.filter(data);
		}
		return data;
	}

	/**
	 * Renders a throwable including stack trace and cause throwable.
	 *
	 * @param throwable
	 *            Throwable to render
	 * @param parentTrace
	 *            Stack trace from parent throwable
	 * @param builder
	 *            Output will be appended to this string builder
	 */
	private void render(final ThrowableData throwable, final List<StackTraceElement> parentTrace, final StringBuilder builder) {
		List<StackTraceElement> stackTrace = throwable.getStackTrace();

		int parentIndex = parentTrace.size() - 1;
		int childIndex = stackTrace.size() - 1;
		int commonElements = 0;
		while (parentIndex >= 0 && childIndex >= 0 && parentTrace.get(parentIndex).equals(stackTrace.get(childIndex))) {
			parentIndex -= 1;
			childIndex -= 1;
			commonElements += 1;
		}
		
		builder.append(throwable.getClassName());
		String message = throwable.getMessage();
		if (message != null) {
			builder.append(": ");
			builder.append(message);
		}

		for (int i = 0; i < stackTrace.size() - commonElements; ++i) {
			builder.append(NEW_LINE);
			builder.append("\tat ");
			builder.append(stackTrace.get(i));
		}
		
		if (commonElements > 0) {
			builder.append(NEW_LINE);
			builder.append("\t... ");
			builder.append(commonElements);
			builder.append(" more");
		}

		ThrowableData cause = throwable.getCause();
		if (cause != null) {
			builder.append(NEW_LINE);
			builder.append("Caused by: ");
			render(cause, stackTrace, builder);
		}
	}

}
