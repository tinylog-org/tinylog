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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * A logging writer that just store policies.
 */
public final class PolicyWriter implements LoggingWriter {

	private final List<? extends Policy> policies;

	/**
	 * @param policy
	 *            Policy to store
	 * @param ignored
	 *            Parameter will be ignored (is only defined to distinguish constructors)
	 */
	public PolicyWriter(final Policy policy, final boolean ignored) {
		if (policy == null) {
			this.policies = Collections.emptyList();
		} else {
			this.policies = Collections.singletonList(policy);
		}
	}

	/**
	 * @param policies
	 *            Policies to store
	 */
	public PolicyWriter(final Policy... policies) {
		this.policies = Arrays.asList(policies);
	}

	/**
	 * Returns the name of the writer.
	 * 
	 * @return "policy"
	 */
	public static String getName() {
		return "policy";
	}

	/**
	 * Returns the supported properties for this writer.
	 * 
	 * @return String array with the properties
	 */
	public static String[][] getSupportedProperties() {
		return new String[][] { new String[] { "policy", "ignored" }, new String[] { "policies" } };
	}

	/**
	 * Returns the stored policies.
	 * 
	 * @return Stored policies
	 */
	public List<? extends Policy> getPolicies() {
		return policies;
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
		// Do nothing
	}

}
