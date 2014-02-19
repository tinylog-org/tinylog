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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Additional collection matchers.
 */
public final class CollectionMatchers {

	private CollectionMatchers() {
	}

	/**
	 * Test if a collection has exactly the same content.
	 * 
	 * @param objects
	 *            Expected objects
	 * @return A matcher that matches the collections
	 */
	@SafeVarargs
	public static Matcher<? super Collection<?>> sameContent(final Object... objects) {
		return new ContentMatcher(Arrays.asList(objects));
	}

	/**
	 * Test if a collection has exactly the same content.
	 * 
	 * @param collection
	 *            Expected objects
	 * @return A matcher that matches the collections
	 */
	public static Matcher<? super Collection<?>> sameContent(final Collection<?> collection) {
		return new ContentMatcher(collection);
	}

	private static final class ContentMatcher extends TypeSafeMatcher<Collection<?>> {

		private final Collection<?> collection;

		private ContentMatcher(final Collection<?> collection) {
			this.collection = collection;
		}

		@Override
		public boolean matchesSafely(final Collection<?> collection) {
			if (this.collection == collection) {
				return true;
			} else if (this.collection == null || collection == null) {
				return false;
			} else if (this.collection.size() != collection.size()) {
				return false;
			} else {
				Iterator<?> iterator1 = this.collection.iterator();
				Iterator<?> iterator2 = collection.iterator();
				while (iterator1.hasNext()) {
					Object object1 = iterator1.next();
					Object object2 = iterator2.next();
					if (object1 != object2) {
						return false;
					}
				}
				return true;
			}
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("contains exactly ");
			if (collection == null) {
				description.appendValue(null);
			} else {
				description.appendValue(collection);
			}
		}
	}

	/**
	 * Test if the items of a collection have the same type.
	 * 
	 * @param objects
	 *            Expected types
	 * @return A matcher that matches the collections
	 */
	@SafeVarargs
	public static Matcher<? super Collection<?>> sameTypes(final Object... objects) {
		return new TypesMatcher(Arrays.asList(objects));
	}

	/**
	 * Test if the items of a collection have the same type.
	 * 
	 * @param collection
	 *            Expected types
	 * @return A matcher that matches the collections
	 */
	public static Matcher<? super Collection<?>> sameTypes(final Collection<?> collection) {
		return new TypesMatcher(collection);
	}

	private static final class TypesMatcher extends TypeSafeMatcher<Collection<?>> {

		private final Collection<?> collection;

		private TypesMatcher(final Collection<?> collection) {
			this.collection = collection;
		}

		@Override
		public boolean matchesSafely(final Collection<?> collection) {
			if (this.collection == collection) {
				return true;
			} else if (this.collection == null || collection == null) {
				return false;
			} else if (this.collection.size() != collection.size()) {
				return false;
			} else {
				Iterator<?> iterator1 = this.collection.iterator();
				Iterator<?> iterator2 = collection.iterator();
				while (iterator1.hasNext()) {
					Object object1 = iterator1.next();
					Object object2 = iterator2.next();
					if (object1 != object2) {
						if (object1 == null || object2 == null) {
							return false;
						} else if (!object1.getClass().equals(object2.getClass())) {
							return false;
						}
					}
				}
				return true;
			}
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("contains exactly the types ");
			if (collection == null) {
				description.appendValue(null);
			} else {
				Collection<Class<?>> classes = new ArrayList<>();
				for (Object object : collection) {
					classes.add(object.getClass());
				}
				description.appendValue(classes);
			}
		}
	}

	/**
	 * Test if the items of a collection are from the expected classes.
	 * 
	 * @param classes
	 *            Expected classes
	 * @return A matcher that matches the collections
	 */
	@SafeVarargs
	public static Matcher<? super Collection<?>> types(final Class<?>... classes) {
		return new ClassMatcher(Arrays.asList(classes));
	}

	/**
	 * Test if the items of a collection are from the expected classes.
	 * 
	 * @param classes
	 *            Expected classes
	 * @return A matcher that matches the collections
	 */
	public static Matcher<? super Collection<?>> types(final Collection<Class<?>> classes) {
		return new ClassMatcher(classes);
	}

	private static final class ClassMatcher extends TypeSafeMatcher<Collection<?>> {

		private final Collection<Class<?>> classes;

		private ClassMatcher(final Collection<Class<?>> classes) {
			this.classes = classes;
		}

		@Override
		public boolean matchesSafely(final Collection<?> collection) {
			if (this.classes == null || collection == null) {
				return false;
			} else if (this.classes.size() != collection.size()) {
				return false;
			} else {
				Iterator<Class<?>> classIterator = this.classes.iterator();
				Iterator<?> objectIterator = collection.iterator();
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
