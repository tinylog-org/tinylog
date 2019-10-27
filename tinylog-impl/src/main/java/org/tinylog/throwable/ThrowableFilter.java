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

package org.tinylog.throwable;

/**
 * Throwable filters transform exceptions and other throwables for improving and customizing the output.
 */
public interface ThrowableFilter {

	/**
	 * Filters or transforms an exception or other throwables.
	 * 
	 * @param origin
	 *            Original exception or other throwable
	 * @return Modified or original throwable
	 */
	ThrowableData filter(ThrowableData origin);

}
