/*
 * Copyright 2017 Martin Winandy
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

package org.pmw.tinylog;

/**
 * This lazy value supplier is identical to {@link java.util.function.Supplier}. It is used by logger for lazy
 * generation of messages and arguments. A message or argument can be provided as lambda expression and will be only
 * generated, if the log entry is really output.
 *
 * <p>
 * tinylog provides its own supplier to ensure compatibility with Java prior to version 8.
 * {@link java.util.function.Supplier} was introduced with Java 8.
 * </p>
 *
 * @param <T>
 *            Type of generated value
 */
public interface Supplier<T> {

	/**
	 * Generate the value.
	 *
	 * @return Generated value
	 */
	T get();

}
