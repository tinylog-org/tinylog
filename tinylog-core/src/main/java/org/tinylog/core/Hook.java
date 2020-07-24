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

package org.tinylog.core;

/**
 * Hooks are called when tinylog is starting and shutting down.
 *
 * <p>
 *     New hooks can either be programmatically registered on {@link Framework} or provided as
 *     {@link java.util.ServiceLoader service} in {@code META-INF/services}.
 * </p>
 */
public interface Hook {

	/**
	 * This method is called when tinylog is starting.
	 */
	void startUp();

	/**
	 * This method is called when tinylog is shutting down.
	 */
	void shutDown();

}
