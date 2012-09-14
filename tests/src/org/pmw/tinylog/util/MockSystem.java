/*
 * Copyright 2012 Martin Winandy
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

import java.util.Properties;

import mockit.Mock;
import mockit.MockClass;

/**
 * Mock for {@link System}.
 */
@MockClass(realClass = System.class)
public final class MockSystem {

	private long time;
	private Properties properties;

	/** */
	public MockSystem() {
		time = 0L;
		properties = new Properties(System.getProperties());
	}

	/**
	 * Get the current time in milliseconds.
	 * 
	 * @return Current time in milliseconds
	 */
	@Mock
	public long currentTimeMillis() {
		return time;
	}

	/**
	 * Set the current time.
	 * 
	 * @param time
	 *            Current time in milliseconds
	 */
	public void setCurrentTimeMillis(final long time) {
		this.time = time;
	}

	/**
	 * Get the current system properties.
	 * 
	 * @return Current system properties
	 */
	@Mock
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Get a system property.
	 * 
	 * @param key
	 *            Key of property to look for
	 * @return Value of requested property
	 */
	@Mock
	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	/**
	 * Get a system property.
	 * 
	 * @param key
	 *            Key of property to look for
	 * @param defaultValue
	 *            Default value if property doesn't exists
	 * @return Value of requested property
	 */
	@Mock
	public String getProperty(final String key, final String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * Set a system property.
	 * 
	 * @param key
	 *            Key of system property
	 * @param value
	 *            Value of system property
	 * @return Previous value
	 */
	@Mock
	public String setProperty(final String key, final String value) {
		return (String) properties.setProperty(key, value);
	}

	/**
	 * Clear a system property.
	 * 
	 * @param key
	 *            Key of system property to remove
	 * @return Value of removed property
	 */
	@Mock
	public String clearProperty(final String key) {
		return (String) properties.remove(key);
	}

	/**
	 * Set the current system properties.
	 * 
	 * @param properties
	 *            New system properties
	 */
	@Mock
	public void setProperties(final Properties properties) {
		this.properties = properties;
	}

}
