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
 * Additional class matchers.
 */
public final class ClassMatchers {

	private ClassMatchers() {
	}

	/**
	 * Test if an object in a direct instance of a class (same class, not a sub class).
	 * 
	 * @param type
	 *            Expected class
	 * @return A matcher that matches any kind of objects
	 */
	public static Matcher<Object> type(final Class<?> type) {
		return new ClassMatcher(type);
	}

	private static final class ClassMatcher extends TypeSafeMatcher<Object> {

		private final Class<?> expected;

		private ClassMatcher(final Class<?> expected) {
			this.expected = expected;
		}

		@Override
		public boolean matchesSafely(final Object instance) {
			if (expected == null || instance == null) {
				return false;
			} else {
				return expected.equals(instance.getClass());
			}
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("is type of ").appendValue(expected);
		}

	}

}
