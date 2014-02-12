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

package org.pmw.tinylog.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.Configurator;

/**
 * Create configuration objects.
 */
public final class ConfigurationCreator {

	private ConfigurationCreator() {
	}

	/**
	 * Get a copy of an empty dummy configuration.
	 * 
	 * @return Dummy configuration
	 */
	public static Configuration getDummyConfiguration() {
		try {
			Method method = Configurator.class.getDeclaredMethod("create");
			method.setAccessible(true);
			return (Configuration) method.invoke(Configurator.defaultConfig().formatPattern("{message}").writer(null).locale(Locale.ROOT));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Get a copy of the default configuration.
	 * 
	 * @return Default configuration
	 */
	public static Configuration getDefaultConfiguration() {
		try {
			Method method = Configurator.class.getDeclaredMethod("create");
			method.setAccessible(true);
			return (Configuration) method.invoke(Configurator.defaultConfig());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

}
