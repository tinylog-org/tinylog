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

package org.tinylog.runtime;

/**
 * Provider for getting the runtime dialect for the current VM.
 */
public final class RuntimeProvider {

	private static final RuntimeDialect dialect = resolveDialect();

	/** */
	private RuntimeProvider() {
	}

	/**
	 * Gets the runtime dialect for the current VM.
	 *
	 * @return Runtime dialect
	 */
	public static RuntimeDialect getDialect() {
		return dialect;
	}

	/**
	 * Resolves the runtime dialect for the current VM.
	 *
	 * @return Resolved runtime dialect
	 */
	private static RuntimeDialect resolveDialect() {
		if ("Android Runtime".equalsIgnoreCase(System.getProperty("java.runtime.name"))) {
			return new AndroidRuntime();
		} else {
			return new JavaRuntime();
		}
	}

}
