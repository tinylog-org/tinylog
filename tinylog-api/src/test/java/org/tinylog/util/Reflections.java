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

package org.tinylog.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utility class for simplifying reflection operations to have readable JUnit tests.
 */
public final class Reflections {

	/** */
	private Reflections() {
	}

	/**
	 * Updates a static field of a class.
	 *
	 * @param type
	 *            Class which contains the field to update
	 * @param name
	 *            Name of the field to update
	 * @param value
	 *            New value for the field
	 * @throws ReflectiveOperationException
	 *             Failed updating
	 */
	public static void updateField(final Class<?> type, final String name, final Object value) throws ReflectiveOperationException {
		Field field = type.getDeclaredField(name);
		field.setAccessible(true);
		removeFinal(field);
		field.set(null, value);
	}

	/**
	 * Updates a member field of an object.
	 *
	 * @param obj
	 *            Object which contains the field to update
	 * @param name
	 *            Name of the field to update
	 * @param value
	 *            New value for the field
	 * @throws ReflectiveOperationException
	 *             Failed updating
	 */
	public static void updateField(final Object obj, final String name, final Object value) throws ReflectiveOperationException {
		Field field = obj.getClass().getDeclaredField(name);
		field.setAccessible(true);
		removeFinal(field);
		field.set(obj, value);
	}

	/**
	 * Removes a final modifier from a field if present.
	 *
	 * @param field
	 *            Field with or without final modifier
	 * @throws ReflectiveOperationException
	 *             Failed removing final modifier
	 */
	private static void removeFinal(final Field field) throws ReflectiveOperationException {
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}

}
