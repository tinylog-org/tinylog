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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Additional string matchers.
 */
public final class StringMatchers {

	private StringMatchers() {
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

}
