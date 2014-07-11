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

package org.pmw.tinylog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Converts a format pattern for log entries to a list of tokens.
 *
 * @see Logger#setLoggingFormat(String)
 */
final class Tokenizer {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String NEW_LINE = EnvironmentHelper.getNewLine();
	private static final Pattern NEW_LINE_REPLACER = Pattern.compile("\r\n|\\\\r\\\\n|\n|\\\\n|\r|\\\\r");
	private static final String TAB = "\t";
	private static final Pattern TAB_REPLACER = Pattern.compile("\t|\\\\t");

	private Tokenizer() {
	}

	/**
	 * Parse a format pattern.
	 *
	 * @param formatPattern
	 *            Format pattern for log entries
	 *
	 * @param locale
	 *            Locale for formatting
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 * @return List of tokens
	 */
	static List<Token> parse(final String formatPattern, final Locale locale, final int maxStackTraceElements) {
		List<Token> tokens = new ArrayList<Token>();

		int start = 0;
		int openMarkers = 0;
		for (int i = 0; i < formatPattern.length(); ++i) {
			char c = formatPattern.charAt(i);
			if (c == '{') {
				if (openMarkers == 0 && start < i) {
					tokens.add(getToken(formatPattern.substring(start, i), locale, maxStackTraceElements));
					start = i;
				}
				++openMarkers;
			} else if (openMarkers > 0 && c == '}') {
				--openMarkers;
				if (openMarkers == 0) {
					tokens.add(getToken(formatPattern.substring(start, i + 1), locale, maxStackTraceElements));
					start = i + 1;
				}
			}
		}

		if (start < formatPattern.length()) {
			tokens.add(getToken(formatPattern.substring(start, formatPattern.length()), locale, maxStackTraceElements));
		}

		return tokens;
	}

	private static Token getToken(final String text, final Locale locale, final int maxStackTraceElements) {
		if (text.startsWith("{date") && text.endsWith("}")) {
			String dateFormatPattern;
			if (text.length() > 6) {
				dateFormatPattern = text.substring(6, text.length() - 1);
			} else {
				dateFormatPattern = DEFAULT_DATE_FORMAT_PATTERN;
			}
			return new DateToken(new SimpleDateFormat(dateFormatPattern, locale));
		} else if ("{pid}".equals(text)) {
			return new PlainTextToken(EnvironmentHelper.getProcessId().toString());
		} else if ("{thread}".equals(text)) {
			return new ThreadNameToken();
		} else if ("{thread_id}".equals(text)) {
			return new ThreadIdToken();
		} else if ("{class}".equals(text)) {
			return new ClassToken();
		} else if ("{class_name}".equals(text)) {
			return new ClassNameToken();
		} else if ("{package}".equals(text)) {
			return new PackageToken();
		} else if ("{method}".equals(text)) {
			return new MethodToken();
		} else if ("{file}".equals(text)) {
			return new FileToken();
		} else if ("{line}".equals(text)) {
			return new LineToken();
		} else if ("{level}".equals(text)) {
			return new LevelToken();
		} else if ("{message}".equals(text)) {
			return new MessageToken(maxStackTraceElements);
		} else {
			String plainText = NEW_LINE_REPLACER.matcher(text).replaceAll(NEW_LINE);
			plainText = TAB_REPLACER.matcher(plainText).replaceAll(TAB);
			return new PlainTextToken(plainText);
		}
	}

	private static final class DateToken implements Token {

		private final DateFormat formatter;

		public DateToken(final DateFormat formatter) {
			this.formatter = formatter;
		}

		@Override
		public TokenType getType() {
			return TokenType.DATE;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(format(logEntry.getDate()));
		}

		private String format(final Date date) {
			synchronized (formatter) {
				return formatter.format(date);
			}
		}

	}

	private static final class ThreadNameToken implements Token {

		public ThreadNameToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.THREAD_NAME;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getThread().getName());
		}

	};

	private static final class ThreadIdToken implements Token {

		public ThreadIdToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.THREAD_ID;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getThread().getId());
		}

	}

	private static final class ClassToken implements Token {

		public ClassToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.CLASS;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getClassName());
		}

	}

	private static final class ClassNameToken implements Token {

		public ClassNameToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.CLASS_NAME;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			String fullyQualifiedClassName = logEntry.getClassName();
			int dotIndex = fullyQualifiedClassName.lastIndexOf('.');
			if (dotIndex < 0) {
				builder.append(fullyQualifiedClassName);
			} else {
				builder.append(fullyQualifiedClassName.substring(dotIndex + 1));
			}
		}

	}

	private static final class PackageToken implements Token {

		public PackageToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.PACKAGE;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			String fullyQualifiedClassName = logEntry.getClassName();
			int dotIndex = fullyQualifiedClassName.lastIndexOf('.');
			if (dotIndex != -1) {
				builder.append(fullyQualifiedClassName.substring(0, dotIndex));
			}
		}

	}

	private static final class MethodToken implements Token {

		public MethodToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.METHOD;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getMethodName());
		}

	}

	private static final class FileToken implements Token {

		public FileToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.FILE;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getFilename());
		}

	}

	private static final class LineToken implements Token {

		public LineToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.LINE;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getLineNumber());
		}

	}

	private static final class LevelToken implements Token {

		public LevelToken() {
		}

		@Override
		public TokenType getType() {
			return TokenType.LEVEL;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getLevel());
		}

	}

	private static final class MessageToken implements Token {

		private static final String NEW_LINE = EnvironmentHelper.getNewLine();

		private final int maxStackTraceElements;

		public MessageToken(final int maxStackTraceElements) {
			this.maxStackTraceElements = maxStackTraceElements;
		}

		@Override
		public TokenType getType() {
			return TokenType.MESSAGE;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			String message = logEntry.getMessage();
			if (message != null) {
				builder.append(message);
			}
			Throwable exception = logEntry.getException();
			if (exception != null) {
				if (message != null) {
					builder.append(": ");
				}
				formatException(builder, exception, maxStackTraceElements);
			}
		}

		private static void formatException(final StringBuilder builder, final Throwable exception, final int maxStackTraceElements) {
			if (maxStackTraceElements == 0) {
				builder.append(exception.getClass().getName());
				String exceptionMessage = exception.getMessage();
				if (exceptionMessage != null) {
					builder.append(": ");
					builder.append(exceptionMessage);
				}
			} else {
				formatExceptionWithStackTrace(builder, exception, maxStackTraceElements);
			}
		}

		private static void formatExceptionWithStackTrace(final StringBuilder builder, final Throwable exception, final int countStackTraceElements) {
			builder.append(exception.getClass().getName());

			String message = exception.getMessage();
			if (message != null) {
				builder.append(": ");
				builder.append(message);
			}

			StackTraceElement[] stackTrace = exception.getStackTrace();
			int length = Math.min(stackTrace.length, Math.max(1, countStackTraceElements));
			for (int i = 0; i < length; ++i) {
				builder.append(NEW_LINE);
				builder.append('\t');
				builder.append("at ");
				builder.append(stackTrace[i]);
			}

			if (stackTrace.length > length) {
				builder.append(NEW_LINE);
				builder.append('\t');
				builder.append("...");
			} else {
				Throwable cause = exception.getCause();
				if (cause != null) {
					builder.append(NEW_LINE);
					builder.append("Caused by: ");
					formatExceptionWithStackTrace(builder, cause, countStackTraceElements - length);
				}
			}
		}

	}

	private static final class PlainTextToken implements Token {

		private final String text;

		public PlainTextToken(final String text) {
			this.text = text;
		}

		@Override
		public TokenType getType() {
			return TokenType.PLAIN_TEXT;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(text);
		}

	}

}
