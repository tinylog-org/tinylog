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
	 * Test if an array has an equal content.
	 * 
	 * @param objects
	 *            Expected objects
	 * @return A matcher that matches the arrays
	 * 
	 * @param <E>
	 *            Type of array elements
	 */
	@SafeVarargs
	public static <E> Matcher<E[]> equalContentInArray(final Object... objects) {
		return new ContentMatcher<E>(Arrays.asList(objects), false);
	}

	/**
	 * Test if an array has exactly the same content.
	 * 
	 * @param objects
	 *            Expected objects
	 * @return A matcher that matches the arrays
	 * 
	 * @param <E>
	 *            Type of array elements
	 */
	@SafeVarargs
	public static <E> Matcher<E[]> sameContentInArray(final Object... objects) {
		return new ContentMatcher<E>(Arrays.asList(objects), true);
	}

	private static final class ContentMatcher<E> extends TypeSafeMatcher<E[]> {

		private final Collection<?> collection;
		private final boolean strict;

		private ContentMatcher(final Collection<?> collection, final boolean strict) {
			this.collection = collection;
			this.strict = strict;
		}

		@Override
		public boolean matchesSafely(final E[] array) {
			if (collection == null || array == null) {
				return false;
			} else if (collection.size() != array.length) {
				return false;
			} else {
				Iterator<?> iterator1 = collection.iterator();
				Iterator<?> iterator2 = Arrays.asList(array).iterator();
				while (iterator1.hasNext()) {
					Object object1 = iterator1.next();
					Object object2 = iterator2.next();
					if (object1 != object2 && (strict || (object1 != null && !object1.equals(object2)))) {
						return false;
					}
				}
				return true;
			}
		}

		@Override
		public void describeTo(final Description description) {
			if (strict) {
				description.appendText("contains exactly ");
			} else {
				description.appendText("contains ");
			}
			if (collection == null) {
				description.appendValue(null);
			} else {
				description.appendValue(collection);
			}
		}
	}

	/**
	 * Test if the items of an array are from the expected classes.
	 * 
	 * @param classes
	 *            Expected classes
	 * @return A matcher that matches the arrays
	 * 
	 * @param <E>
	 *            Type of array elements
	 */
	@SafeVarargs
	public static <E> Matcher<E[]> typesInArray(final Class<?>... classes) {
		return new ClassMatcher<E>(Arrays.asList(classes));
	}

	private static final class ClassMatcher<E> extends TypeSafeMatcher<E[]> {

		private final Collection<Class<?>> classes;

		private ClassMatcher(final Collection<Class<?>> classes) {
			this.classes = classes;
		}

		@Override
		public boolean matchesSafely(final E[] array) {
			if (classes.size() != array.length) {
				return false;
			} else {
				Iterator<Class<?>> classIterator = classes.iterator();
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
