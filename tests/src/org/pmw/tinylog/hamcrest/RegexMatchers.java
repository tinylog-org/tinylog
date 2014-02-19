/*
 * Copyright 2013 Martin Winandy
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
 * Matches strings against regular expressions.
 * 
 * @see Pattern
 */
public final class RegexMatchers {

	private RegexMatchers() {
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

	/**
	 * Match a string against a regular expression.
	 * 
	 * @param regularExpression
	 *            Regular expression to match
	 * 
	 * @return A matcher that matches the input string
	 */
	public static Matcher<String> matches(final String regularExpression) {
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
	public static Matcher<String> contains(final String regularExpression) {
		return new ContainsRegexpMatcher(regularExpression);
	}

}
