/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.runtime;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Base class for runtime dialect implementations for Java from Oracle and OpenJDK.
 */
abstract class AbstractJavaRuntime implements RuntimeDialect {

	private final boolean hasSunReflection;

	/** */
	AbstractJavaRuntime() {
		hasSunReflection = verifySunReflection();
	}

	@Override
	public String getDefaultWriter() {
		return "console";
	}

	/**
	 * Gets availability of {@link sun.reflect.Reflection#getCallerClass(int)}.
	 *
	 * @return {@code true} if Sun Reflection is available, {@code false} if not
	 */
	@SuppressWarnings("javadoc")
	protected final boolean isSunReflectionAvailable() {
		return hasSunReflection;
	}

	/**
	 * Checks whether {@link sun.reflect.Reflection#getCallerClass(int)} is available.
	 *
	 * @return {@code true} if available, {@code true} if not
	 */
	@SuppressWarnings({ "deprecation", "javadoc" })
	@IgnoreJRERequirement
	private static boolean verifySunReflection() {
		try {
			return AbstractJavaRuntime.class.equals(sun.reflect.Reflection.getCallerClass(1));
		} catch (NoClassDefFoundError error) {
			return false;
		} catch (NoSuchMethodError error) {
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

}
