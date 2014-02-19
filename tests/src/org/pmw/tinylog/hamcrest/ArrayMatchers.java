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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Additional array matchers.
 */
public final class ArrayMatchers {

	private ArrayMatchers() {
	}

	/**
	 * Test if the items of an array are from the expected classes.
	 * 
	 * @param classes
	 *            Expected classes
	 * @return A matcher that matches the array
	 */
	@SafeVarargs
	public static Matcher<Object[]> types(final Class<?>... classes) {
		return new ClassMatcher(Arrays.asList(classes));
	}

	/**
	 * Test if the items of an array are from the expected classes.
	 * 
	 * @param classes
	 *            Expected classes
	 * @return A matcher that matches the array
	 */
	public static Matcher<Object[]> types(final Collection<Class<?>> classes) {
		return new ClassMatcher(classes);
	}

	private static final class ClassMatcher extends TypeSafeMatcher<Object[]> {

		private final Collection<Class<?>> classes;

		private ClassMatcher(final Collection<Class<?>> classes) {
			this.classes = classes;
		}

		@Override
		public boolean matchesSafely(final Object[] array) {
			if (this.classes == null || array == null) {
				return false;
			} else if (this.classes.size() != array.length) {
				return false;
			} else {
				Iterator<Class<?>> classIterator = this.classes.iterator();
				Iterator<?> objectIterator = Arrays.asList(array).iterator();
				while (classIterator.hasNext()) {
					Class<?> clazz = classIterator.next();
					Object object = objectIterator.next();
					if (clazz == null || object == null) {
						return false;
					} else if (!clazz.equals(object.getClass())) {
						return false;
					}
				}
				return true;
			}
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("contains exactly the types ");
			description.appendValue(classes);
		}

	}

}
