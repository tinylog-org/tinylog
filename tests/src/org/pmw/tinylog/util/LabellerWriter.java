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

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * A logging writer that just store labellers.
 */
public final class LabellerWriter implements LoggingWriter {

	private final Labeller labeller;

	/**
	 * @param labeller
	 *            Labeller to store
	 */
	public LabellerWriter(final Labeller labeller) {
		this.labeller = labeller;
	}

	/**
	 * Returns the name of the writer.
	 * 
	 * @return "labeller"
	 */
	public static String getName() {
		return "labeller";
	}

	/**
	 * Returns the supported properties ("labeling") for this writer.
	 * 
	 * @return String array with the property "labeling"
	 */
	public static String[][] getSupportedProperties() {
		return new String[][] { new String[] { "labeling" } };
	}

	/**
	 * Returns the stored labeller.
	 * 
	 * @return Stored labeller
	 */
	public Labeller getLabeller() {
		return labeller;
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
		// Do nothing
	}

}
