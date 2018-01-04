/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.path;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tinylog.runtime.RuntimeProvider;

/**
 * Dynamic process ID path segment.
 */
public final class ProcessIdSegment extends AbstractSegment {

	private final String processId;

	/**
	 * @param next
	 *            Successor path segment
	 */
	public ProcessIdSegment(final Segment next) {
		super(next);
		processId = Integer.toString(RuntimeProvider.getProcessId());
	}

	@Override
	public String getLatestFile(final String prefix) {
		return null;
	}

	@Override
	public final Collection<String> getAllFiles(final String prefix) {
		String dictionary = getDictionary(prefix);
		String[] entries = new File(dictionary).list();
		if (entries == null || entries.length == 0) {
			return Collections.emptyList();
		} else {
			List<String> files = new ArrayList<String>();
			String namePrefix = prefix.substring(dictionary.length());
			for (String value : getValues(namePrefix, entries)) {
				files.addAll(getNext().getAllFiles(prefix + value));
			}
			return files;
		}
	}

	@Override
	public String createNewFile(final String prefix) {
		return getNext().createNewFile(prefix + processId);
	}

	/**
	 * Collects all (partial) filenames that start with the passed prefix and continue with digits.
	 * 
	 * @param prefix
	 *            Prefix for matches
	 * @param entries
	 *            Found filenames
	 * @return Matched partial filenames without passed prefix
	 */
	private static Set<String> getValues(final String prefix, final String[] entries) {
		Set<String> values = new HashSet<String>();
		for (String entry : entries) {
			if (entry.startsWith(prefix) && entry.length() > prefix.length()) {
				String value = getNumericPart(entry.substring(prefix.length()));
				if (value != null) {
					values.add(value);
				}
			}
		}
		return values;
	}

	/**
	 * Gets all digits from the beginning of a textual value.
	 * 
	 * @param value
	 *            Text that potentially starts with a number
	 * @return All digits at the beginning of the passed string, or {@code null} if there are none
	 */
	private static String getNumericPart(final String value) {
		for (int i = 0; i < value.length(); ++i) {
			char character = value.charAt(i);
			if (character < '0' || character > '9') {
				return i == 0 ? null : value.substring(0, i);
			}
		}

		return value;
	}

}
