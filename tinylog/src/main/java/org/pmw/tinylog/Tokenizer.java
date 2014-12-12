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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.pmw.tinylog.writers.LogEntryValue;

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

	private final Locale locale;
	private final int maxStackTraceElements;

	private int index;

	/**
	 * @param locale
	 *            Locale for formatting
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 */
	Tokenizer(final Locale locale, final int maxStackTraceElements) {
		this.locale = locale;
		this.maxStackTraceElements = maxStackTraceElements;
	}

	/**
	 * Parse a format pattern.
	 *
	 * @param formatPattern
	 *            Format pattern for log entries
	 *
	 * @return List of tokens
	 */
	List<Token> parse(final String formatPattern) {
		List<Token> tokens = new ArrayList<>();
		index = 0;

		while (index < formatPattern.length()) {
			char c = formatPattern.charAt(index);

			int start = index;
			while (c != '{' && c != '}') {
				++index;
				if (index >= formatPattern.length()) {
					tokens.add(getPlainTextToken(formatPattern.substring(start, index)));
					return tokens;
				}
				c = formatPattern.charAt(index);
			}
			if (index > start) {
				tokens.add(getPlainTextToken(formatPattern.substring(start, index)));
			}

			if (c == '{') {
				Token token = parsePartly(formatPattern);
				if (token != null) {
					tokens.add(token);
				}
			} else if (c == '}') {
				InternalLogger.warn("Opening curly brace is missing for: \"{}\"", formatPattern.substring(0, index + 1));
				++index;
			}
		}

		return tokens;
	}

	private Token parsePartly(final String formatPattern) {
		List<Token> tokens = new ArrayList<>();
		int[] options = new int[] { 0 /* minimum size */, 0 /* indent */};
		int offset = index;

		++index;
		while (index < formatPattern.length()) {
			char c = formatPattern.charAt(index);

			int start = index;
			while (c != '{' && c != '|' && c != '}') {
				++index;
				if (index >= formatPattern.length()) {
					InternalLogger.warn("Closing curly brace is missing for: \"{}\"", formatPattern.substring(offset, index));
					tokens.add(getToken(formatPattern.substring(start, index)));
					return combine(tokens, options);
				}
				c = formatPattern.charAt(index);
			}
			if (index > start) {
				if (c == '{') {
					tokens.add(getPlainTextToken(formatPattern.substring(start, index)));
				} else {
					tokens.add(getToken(formatPattern.substring(start, index)));
				}
			}

			if (c == '{') {
				Token token = parsePartly(formatPattern);
				if (token != null) {
					tokens.add(token);
				}
			} else if (c == '|') {
				++index;
				start = index;
				while (c != '{' && c != '}') {
					++index;
					if (index >= formatPattern.length()) {
						InternalLogger.warn("Closing curly brace is missing for: \"{}\"", formatPattern.substring(offset, index));
						options = parseOptions(formatPattern.substring(start));
						return combine(tokens, options);
					}
					c = formatPattern.charAt(index);
				}
				if (index > start) {
					options = parseOptions(formatPattern.substring(start, index));
				}
			} else if (c == '}') {
				++index;
				return combine(tokens, options);
			}
		}

		InternalLogger.warn("Closing curly brace is missing for: \"{}\"", formatPattern.substring(offset, index));
		return combine(tokens, options);
	}

	private Token getToken(final String text) {
		if (text.equals("date")) {
			return new DateToken(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN, locale));
		} else if (text.startsWith("date:")) {
			String dateFormatPattern = text.substring(5, text.length());
			try {
				return new DateToken(DateTimeFormatter.ofPattern(dateFormatPattern, locale));
			} catch (IllegalArgumentException ex) {
				InternalLogger.error(ex, "\"{}\" is an invalid date format pattern", dateFormatPattern);
				return new DateToken(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN, locale));
			}
		} else if ("pid".equals(text)) {
			return new PlainTextToken(EnvironmentHelper.getProcessId().toString());
		} else if (text.startsWith("pid:")) {
			InternalLogger.warn("\"{pid}\" does not support parameters");
			return new PlainTextToken(EnvironmentHelper.getProcessId().toString());
		} else if ("thread".equals(text)) {
			return new ThreadNameToken();
		} else if (text.startsWith("thread:")) {
			InternalLogger.warn("\"{thread}\" does not support parameters");
			return new ThreadNameToken();
		} else if ("thread_id".equals(text)) {
			return new ThreadIdToken();
		} else if (text.startsWith("thread_id:")) {
			InternalLogger.warn("\"{thread_id}\" does not support parameters");
			return new ThreadIdToken();
		} else if ("class".equals(text)) {
			return new ClassToken();
		} else if (text.startsWith("class:")) {
			InternalLogger.warn("\"{class}\" does not support parameters");
			return new ClassToken();
		} else if ("class_name".equals(text)) {
			return new ClassNameToken();
		} else if (text.startsWith("class_name:")) {
			InternalLogger.warn("\"{class_name}\" does not support parameters");
			return new ClassNameToken();
		} else if ("package".equals(text)) {
			return new PackageToken();
		} else if (text.startsWith("package:")) {
			InternalLogger.warn("\"{package}\" does not support parameters");
			return new PackageToken();
		} else if ("method".equals(text)) {
			return new MethodToken();
		} else if (text.startsWith("method:")) {
			InternalLogger.warn("\"{method}\" does not support parameters");
			return new MethodToken();
		} else if ("file".equals(text)) {
			return new FileToken();
		} else if (text.startsWith("file:")) {
			InternalLogger.warn("\"{file}\" does not support parameters");
			return new FileToken();
		} else if ("line".equals(text)) {
			return new LineToken();
		} else if (text.startsWith("line:")) {
			InternalLogger.warn("\"{line}\" does not support parameters");
			return new LineToken();
		} else if ("level".equals(text)) {
			return new LevelToken();
		} else if (text.startsWith("level:")) {
			InternalLogger.warn("\"{level}\" does not support parameters");
			return new LevelToken();
		} else if ("message".equals(text)) {
			return new MessageToken(maxStackTraceElements);
		} else if (text.startsWith("message:")) {
			InternalLogger.warn("\"{message}\" does not support parameters");
			return new MessageToken(maxStackTraceElements);
		} else {
			return getPlainTextToken(text);
		}
	}

	private static Token getPlainTextToken(final String text) {
		String plainText = NEW_LINE_REPLACER.matcher(text).replaceAll(NEW_LINE);
		plainText = TAB_REPLACER.matcher(plainText).replaceAll(TAB);
		return new PlainTextToken(plainText);
	}

	private static int[] parseOptions(final String text) {
		int minSize = 0;
		int indent = 0;

		int index = 0;
		while (index < text.length()) {
			char c = text.charAt(index);

			while (c == ',') {
				++index;
				if (index >= text.length()) {
					return new int[] { minSize, indent };
				}
				c = text.charAt(index);
			}

			int start = index;
			while (c != ',') {
				++index;
				if (index >= text.length()) {
					break;
				}
				c = text.charAt(index);
			}
			if (index > start) {
				String parameter = text.substring(start, index);
				int splitter = parameter.indexOf('=');
				if (splitter == -1) {
					parameter = parameter.trim();
					if ("min-size".equals(parameter)) {
						InternalLogger.warn("No value set for \"min-size\"");
					} else if ("indent".equals(parameter)) {
						InternalLogger.warn("No value set for \"indent\"");
					} else  {
						InternalLogger.warn("Unknown option \"{}\"", parameter);
					}
				} else {
					String key = parameter.substring(0, splitter).trim();
					String value = parameter.substring(splitter + 1).trim();
					if ("min-size".equals(key)) {
						if (value.length() == 0) {
							InternalLogger.warn("No value set for \"min-size\"");
						} else {
							try {
								minSize = parsePositiveInt(value);
							} catch (NumberFormatException ex) {
								InternalLogger.warn("\"{}\" is an invalid number for \"min-size\"", value);
							}
						}
					} else if ("indent".equals(key)) {
						if (value.length() == 0) {
							InternalLogger.warn("No value set for \"indent\"");
						} else {
							try {
								indent = parsePositiveInt(value);
							} catch (NumberFormatException ex) {
								InternalLogger.warn("\"{}\" is an invalid number for \"indent\"", value);
							}
						}
					} else {
						InternalLogger.warn("Unknown option \"{}\"", key);
					}
				}
			}
		}

		return new int[] { minSize, indent };
	}

	private static int parsePositiveInt(final String value) throws NumberFormatException {
		int number = Integer.parseInt(value);
		if (number >= 0) {
			return number;
		} else {
			throw new NumberFormatException();
		}
	}

	private static Token combine(final List<Token> tokens, final int[] options) {
		int minSize = options[0];
		int indent = options[1];

		if (tokens.isEmpty()) {
			return null;
		} else if (tokens.size() == 1) {
			if (indent > 0) {
				return new IndentToken(tokens.get(0), indent);
			} else if (minSize > 0) {
				return new MinSizeToken(tokens.get(0), minSize);
			} else {
				return tokens.get(0);
			}
		} else {
			if (indent > 0) {
				return new IndentToken(new BundlerToken(tokens), indent);
			} else if (minSize > 0) {
				return new MinSizeToken(new BundlerToken(tokens), minSize);
			} else {
				return new BundlerToken(tokens);
			}
		}
	}

	private static final class BundlerToken implements Token {

		private final List<Token> tokens;

		private BundlerToken(final List<Token> tokens) {
			this.tokens = tokens;
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
			for (Token token : tokens) {
				values.addAll(token.getRequiredLogEntryValues());
			}
			return values;
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			for (Token token : tokens) {
				token.render(logEntry, builder);
			}
		}

	}

	private static final class MinSizeToken implements Token {

		private final Token token;
		private final int minSize;

		private MinSizeToken(final Token token, final int minSize) {
			this.token = token;
			this.minSize = minSize;
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return token.getRequiredLogEntryValues();
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			int offset = builder.length();
			token.render(logEntry, builder);
			int size = builder.length() - offset;
			if (size < minSize) {
				char[] spaces = new char[minSize - size];
				Arrays.fill(spaces, ' ');
				builder.append(spaces);
			}
		}

	}

	private static final class IndentToken implements Token {

		private final Token token;
		private final char[] spaces;

		private IndentToken(final Token token, final int indent) {
			this.token = token;
			this.spaces = new char[indent];
			Arrays.fill(spaces, ' ');
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return token.getRequiredLogEntryValues();
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			if (builder.length() == 0 || builder.charAt(builder.length() - 1) == '\n' || builder.charAt(builder.length() - 1) == '\r') {
				builder.append(spaces);
			}

			StringBuilder subBuilder = new StringBuilder(1024);
			token.render(logEntry, subBuilder);

			int head = 0;
			for (int i = head; i < subBuilder.length(); ++i) {
				char c = subBuilder.charAt(i);
				if (c == '\n') {
					builder.append(subBuilder, head, i + 1);
					builder.append(spaces);
					head = i + 1;
				} else if (c == '\r') {
					if (i + 1 < subBuilder.length() && subBuilder.charAt(i + 1) == '\n') {
						++i;
					}
					builder.append(subBuilder, head, i + 1);
					builder.append(spaces);
					head = i + 1;
				} else if (head == i && (c == ' ' || c == '\t')) {
					++head;
				}
			}
			if (head < subBuilder.length()) {
				builder.append(subBuilder, head, subBuilder.length());
			}
		}

	}

	private static final class DateToken implements Token {

		private final DateTimeFormatter formatter;

		private DateToken(final DateTimeFormatter formatter) {
			this.formatter = formatter;
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.DATE);
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(formatter.format(logEntry.getDate()));
		}

	}

	private static final class ThreadNameToken implements Token {

		private ThreadNameToken() {
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.THREAD);
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
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.THREAD);
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
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.CLASS);
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
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.CLASS);
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

		private PackageToken() {
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.CLASS);
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

		private MethodToken() {
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.METHOD);
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getMethodName());
		}

	}

	private static final class FileToken implements Token {

		private FileToken() {
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.FILE);
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getFilename());
		}

	}

	private static final class LineToken implements Token {

		private LineToken() {
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.LINE);
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getLineNumber());
		}

	}

	private static final class LevelToken implements Token {

		private LevelToken() {
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.LEVEL);
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(logEntry.getLevel());
		}

	}

	private static final class MessageToken implements Token {

		private static final String NEW_LINE = EnvironmentHelper.getNewLine();

		private final int maxStackTraceElements;

		private MessageToken(final int maxStackTraceElements) {
			this.maxStackTraceElements = maxStackTraceElements;
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.singletonList(LogEntryValue.MESSAGE);
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

		private PlainTextToken(final String text) {
			this.text = text;
		}

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.emptyList();
		}

		@Override
		public void render(final LogEntry logEntry, final StringBuilder builder) {
			builder.append(text);
		}

	}

}
