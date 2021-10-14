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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.tinylog.Level;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.provider.InternalLogger;
import org.tinylog.throwable.ThrowableFilter;

/**
 * Parser for format patterns. It produces tokens combined to a root token that can be used by writers for rendering log
 * entries.
 */
public final class FormatPatternParser {

	private static final Pattern SPLIT_PATTERN = Pattern.compile(",");

	private final List<ThrowableFilter> filters;

	/**
	 * @param filters
	 *            Comma separated list of throwable filters
	 */
	public FormatPatternParser(final String filters) {
		if (filters == null) {
			this.filters = Collections.emptyList();
		} else {
			this.filters = new ServiceLoader<ThrowableFilter>(ThrowableFilter.class, String.class).createList(filters);
		}
	}

	/**
	 * Parses a format pattern and produces a generic token from it. This token can be used by writers for rendering log
	 * entries.
	 *
	 * @param pattern
	 *            Format pattern
	 * @return Produced root token
	 */
	public Token parse(final String pattern) {
		List<Token> tokens = new ArrayList<Token>();

		int start = 0;
		int count = 0;

		for (int i = 0; i < pattern.length(); ++i) {
			char character = pattern.charAt(i);

			if (character == '{') {
				if (count == 0) {
					if (start < i) {
						tokens.add(new PlainTextToken(pattern.substring(start, i)));
					}
					start = i;
				}
				count += 1;
			} else if (character == '}') {
				if (count == 0) {
					InternalLogger.log(Level.ERROR, "Opening curly bracket is missing: '" + pattern + "'");
				} else {
					count -= 1;
					if (count == 0) {
						tokens.add(parse(pattern.substring(start + 1, i)));
						start = i + 1;
					}
				}
			}
		}

		if (count > 0) {
			InternalLogger.log(Level.ERROR, "Closing curly bracket is missing: '" + pattern + "'");
		}

		int splitIndex = pattern.indexOf('|', start);
		if (splitIndex == -1) {
			tokens.add(createPlainToken(pattern.substring(start)));
			return tokens.size() == 1 ? tokens.get(0) : new BundleToken(tokens);
		} else {
			String token = pattern.substring(start, splitIndex).trim();
			tokens.add(createPlainToken(token));
			String[] styleOptions = SPLIT_PATTERN.split(pattern.substring(splitIndex + 1));
			return styleToken(tokens.size() == 1 ? tokens.get(0) : new BundleToken(tokens), styleOptions);
		}
	}

	/**
	 * Creates a new token for a given placeholder.
	 *
	 * @param placeholder
	 *            Placeholder without style options and surrounding curly brackets
	 * @return Created token
	 */
	private Token createPlainToken(final String placeholder) {
		int splitIndex = placeholder.indexOf(':');
		Token token;

		if (splitIndex == -1) {
			token = createPlainToken(placeholder.trim(), null);
		} else {
			String name = placeholder.substring(0, splitIndex).trim();
			String configuration = placeholder.substring(splitIndex + 1).trim();
			token = createPlainToken(name, configuration);
		}

		return token == null ? new PlainTextToken(placeholder) : token;
	}

	/**
	 * Creates a new token for a name and configuration.
	 *
	 * @param name
	 *            Name of token
	 * @param configuration
	 *            Configuration for token or {@code null} if not defined
	 * @return Created token or {@code null} if there is no token for the passed parameters
	 */
	private Token createPlainToken(final String name, final String configuration) {
		if (name.equals("date")) {
			return createDateToken(configuration);
		} else if ("timestamp".equals(name)) {
			return new TimestampToken(configuration);
		} else if ("uptime".equals(name)) {
			return configuration == null ? new UptimeToken() : new UptimeToken(configuration);
		} else if ("pid".equals(name)) {
			return new ProcessIdToken();
		} else if ("thread".equals(name)) {
			return new ThreadNameToken();
		} else if ("thread-id".equals(name)) {
			return new ThreadIdToken();
		} else if ("context".equals(name)) {
			return createThreadContextToken(configuration);
		} else if ("class".equals(name)) {
			return new FullClassNameToken();
		} else if ("class-name".equals(name)) {
			return new SimpleClassNameToken();
		} else if ("package".equals(name)) {
			return new PackageNameToken();
		} else if ("method".equals(name)) {
			return new MethodNameToken();
		} else if ("file".equals(name)) {
			return new FileNameToken();
		} else if ("line".equals(name)) {
			return new LineNumberToken();
		} else if ("tag".equals(name)) {
			return configuration == null ? new LoggerTagToken() : new LoggerTagToken(configuration);
		} else if ("level".equals(name)) {
			return new SeverityLevelToken();
		} else if ("level-code".equals(name)) {
			return new SeverityLevelIntegerToken();
		} else if ("message".equals(name)) {
			return new MessageAndExceptionToken(filters);
		} else if ("message-only".equals(name)) {
			return new MessageToken();
		} else if ("exception".equals(name)) {
			return new ExceptionToken(filters);
		} else if ("opening-curly-bracket".equals(name)) {
			return new PlainTextToken("{");
		} else if ("closing-curly-bracket".equals(name)) {
			return new PlainTextToken("}");
		} else if ("pipe".equals(name)) {
			return new PlainTextToken("|");
		} else {
			return null;
		}
	}

	/**
	 * Creates a new {@link DateToken}.
	 *
	 * @param configuration
	 *            Defined format pattern or {@code null} for default pattern
	 * @return New instance of {@link DateToken}
	 */
	private static Token createDateToken(final String configuration) {
		if (configuration == null) {
			return new DateToken();
		} else {
			try {
				return new DateToken(configuration);
			} catch (IllegalArgumentException ex) {
				InternalLogger.log(Level.ERROR, "'" + configuration + "' is an invalid date format pattern");
				return new DateToken();
			}
		}
	}

	/**
	 * Creates a new {@link ThreadContextToken}.
	 *
	 * @param configuration
	 *            Key and optional placeholder for empty values
	 * @return New instance of {@link ThreadContextToken} or {@code null} if key is missing
	 */
	private static Token createThreadContextToken(final String configuration) {
		if (configuration == null) {
			InternalLogger.log(Level.ERROR, "\"{context}\" requires a key");
			return new PlainTextToken("");
		} else {
			int splitIndex = configuration.indexOf(',');
			String key = splitIndex == -1 ? configuration.trim() : configuration.substring(0, splitIndex).trim();
			if (key.isEmpty()) {
				InternalLogger.log(Level.ERROR, "\"{context}\" requires a key");
				return new PlainTextToken("");
			} else {
				String defaultValue = splitIndex == -1 ? null : configuration.substring(splitIndex + 1).trim();
				return defaultValue == null ? new ThreadContextToken(key) : new ThreadContextToken(key, defaultValue);
			}
		}
	}

	/**
	 * Creates style decorators for a token.
	 *
	 * @param token
	 *            Token to style
	 * @param options
	 *            Style options
	 * @return Styled token
	 */
	private static Token styleToken(final Token token, final String[] options) {
		Token styledToken = token;

		for (String option : options) {
			int splitIndex = option.indexOf('=');
			if (splitIndex == -1) {
				InternalLogger.log(Level.ERROR, "No value set for '" + option.trim() + "'");
			} else {
				String key = option.substring(0, splitIndex).trim();
				String value = option.substring(splitIndex + 1).trim();

				int number;
				try {
					number = parsePositiveInteger(value);
				} catch (NumberFormatException ex) {
					InternalLogger.log(Level.ERROR, "'" + value + "' is an invalid value for '" + key + "'");
					continue;
				}

				if ("min-size".equals(key)) {
					styledToken = new MinimumSizeToken(styledToken, number);
				} else if ("max-size".equals(key)) {
					styledToken = new MaximumSizeToken(styledToken, number);
				} else if ("size".equals(key)) {
					styledToken = new SizeToken(styledToken, number);
				} else if ("indent".equals(key)) {
					styledToken = new IndentationToken(styledToken, number);
				} else {
					InternalLogger.log(Level.ERROR, "Unknown style option: '" + key + "'");
				}
			}
		}

		return styledToken;
	}

	/**
	 * Parses a positive integer. In opposite to {@link Integer#parseInt(String)}, this method throws a
	 * {@link NumberFormatException} for negative values.
	 *
	 * @param value
	 *            Number as text
	 * @return Parsed integer
	 * @throws NumberFormatException
	 *             Text doesn't contain a valid positive integer
	 */
	private static int parsePositiveInteger(final String value) throws NumberFormatException {
		int number = Integer.parseInt(value);
		if (number >= 0) {
			return number;
		} else {
			throw new NumberFormatException();
		}
	}

}
