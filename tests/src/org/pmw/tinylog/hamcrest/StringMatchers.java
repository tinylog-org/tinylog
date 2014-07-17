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

package org.pmw.tinylog.hamcrest;

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Additional string matchers.
 */
public final class StringMatchers {

	private StringMatchers() {
	}

	/**
	 * Check the length of a string.
	 *
	 * @param expectedLength
	 *            Expected length of a string
	 * @return A matcher that checks the string length
	 */
	public static Matcher<String> hasLength(final int expectedLength) {
		return new LengthMatcher(expectedLength);
	}

	/**
	 * Match a string against a regular expression.
	 *
	 * @param regularExpression
	 *            Regular expression to match
	 *
	 * @return A matcher that matches the input string
	 */
	public static Matcher<String> matchesPattern(final String regularExpression) {
		return new MatchesRegexpMatcher(regularExpression);
	}

	/**
	 * Find a regular expression in a string.
	 *
	 * @param regularExpression
	 *            Regular expression to find
	 *
	 * @return A matcher that finds the regular expression
	 */
	public static Matcher<String> containsPattern(final String regularExpression) {
		return new ContainsRegexpMatcher(regularExpression);
	}

	private static final class LengthMatcher extends TypeSafeMatcher<String> {

		private final int expectedLength;

		private LengthMatcher(final int expectedLength) {
			this.expectedLength = expectedLength;
		}

		@Override
		public boolean matchesSafely(final String string) {
			return string != null && string.length() == expectedLength;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("a string with the length " + expectedLength);
		}
	}

	private abstract static class AbstractRegexpMatcher extends TypeSafeMatcher<String> {

		private final String regularExpression;
		private final Pattern pattern;

		private AbstractRegexpMatcher(final String regularExpression) {
			this.regularExpression = regularExpression;
			this.pattern = Pattern.compile(regularExpression);
		}

		protected final String getRegularExpression() {
			return regularExpression;
		}

		protected final Pattern getPattern() {
			return pattern;
		}

	}

	private static final class MatchesRegexpMatcher extends AbstractRegexpMatcher {

		private MatchesRegexpMatcher(final String regularExpression) {
			super(regularExpression);
		}

		@Override
		public boolean matchesSafely(final String text) {
			return getPattern().matcher(text).matches();
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("matches regular expression ").appendValue(getRegularExpression());
		}

	}

	private static final class ContainsRegexpMatcher extends AbstractRegexpMatcher {

		private ContainsRegexpMatcher(final String regularExpression) {
			super(regularExpression);
		}

		@Override
		public boolean matchesSafely(final String text) {
			return getPattern().matcher(text).find();
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("contains regular expression ").appendValue(getRegularExpression());
		}

	}

}
