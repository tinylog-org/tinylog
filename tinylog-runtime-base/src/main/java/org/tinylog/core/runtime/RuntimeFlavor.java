/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.runtime;

import java.io.Serializable;
import java.util.Locale;

/**
 * Abstraction of API that depends on the Java version or flavor.
 *
 * @param <T> Date type that is used under the hood for {@link Timestamp Timestamps}
 */
public interface RuntimeFlavor<T extends Serializable & Comparable<T>> {

	/**
	 * Creates a timestamp with the current date and time.
	 *
	 * @return Created timestamp with the current date and time
	 */
	Timestamp<T> createTimestamp();

	/**
	 * Creates a timestamp formatter that can format timestamps created by this runtime flavor.
	 *
	 * @param pattern Date and time pattern
	 * @param locale Locale for language or country depending format outputs
	 * @return Created timestamp formatter
	 */
	TimestampFormatter<T> createTimestampFormatter(String pattern, Locale locale);

}
