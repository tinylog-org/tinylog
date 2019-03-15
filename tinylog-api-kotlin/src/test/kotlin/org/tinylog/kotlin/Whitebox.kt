/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.kotlin

import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * Reflection utility class for accessing private members.
 */
object Whitebox {

	/**
	 * Gets the value of a property by name.
	 *
	 * @param instance
	 * Instance of a type that has the requested property
	 * @param name
	 * Name of the property to get
	 * @return
	 * Current value
	 */
	fun <T : Any> getProperty(instance: T, name: String): Any? {
		return instance::class.memberProperties.filter { it.name == name }.onEach { it.isAccessible = true }.first().call(instance)
	}

	/**
	 * Sets a value to a property by type.
	 *
	 * @param instance
	 * Instance of a type that has the requested property
	 * @param type
	 * Type of the property to change
	 * @param value
	 * New value to set
	 */
	fun <T : Any> setProperty(instance: Any, type: KClass<T>, value: T?) {
		return instance::class.memberProperties.mapNotNull { it.javaField }.filter { it.type == type.java }.forEach {
			setProperty(instance, it, value)
		}
	}

	/**
	 * Sets a value to a property by name.
	 *
	 * @param instance
	 * Instance of a type that has the requested property
	 * @param name
	 * Name of the property to change
	 * @param value
	 * New value to set
	 */
	fun <T : Any> setProperty(instance: Any, name: String, value: T?) {
		return instance::class.memberProperties.filter { it.name == name }.mapNotNull { it.javaField }.forEach {
			setProperty(instance, it, value)
		}
	}

	/**
	 * Calls a private method by name.
	 *
	 * @param instance
	 * Instance of a type that has the requested method
	 * @param name
	 * Name of the method to call
	 * @param arguments
	 * Arguments for the method
	 * @return
	 * Return value of the called method
	 */
	fun callMethod(instance: Any, name: String, vararg arguments: Any?): Any? {
		return instance::class.memberFunctions.filter {
				it.name == name
			}.onEach {
				it.isAccessible = true
			}.first().call(instance, *arguments)
	}

	/**
	 * Sets a value to a field.
	 *
	 * @param instance
	 * Object to update
	 * @param field
	 * Property to update
	 * @param value
	 * New value to set
	 */
	private fun <T : Any> setProperty(instance: Any, field: Field, value: T?) {
		FieldUtils.removeFinalModifier(field, true)
		if (Modifier.isStatic(field.modifiers)) {
			FieldUtils.writeStaticField(field, value, true)
		} else {
			FieldUtils.writeField(field, instance, value, true)
		}
	}

}
