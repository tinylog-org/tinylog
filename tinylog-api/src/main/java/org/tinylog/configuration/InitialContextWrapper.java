/*
 * Copyright 2022 Martin Winandy
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

package org.tinylog.configuration;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Wrapper for {@link InitialContext} for preventing {@link java.lang.VerifyError} on legacy Android versions.
 */
final class InitialContextWrapper {

	/** */
	private InitialContextWrapper() {
	}

	/**
	 * Looks up the textual value for a given name from {@link InitialContext} without throwing any
	 * {@link NamingException}.
	 *
	 * @param name The name for the expected value
	 * @return The stored textual value or {@code null} if none found
	 */
	static String resolve(final String name) {
		try {
			Object value = InitialContext.doLookup(name);
			return value == null ? null : value.toString();
		} catch (NameNotFoundException ex) {
			return null;
		} catch (NamingException ex) {
			InternalLogger.log(Level.ERROR, ex, "Failed to look up \"" + name + "\"");
			return null;
		}
	}

}
