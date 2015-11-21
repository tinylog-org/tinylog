/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.labelers;

import java.io.File;
import java.io.FileFilter;

/**
 * Filters files by a given prefix and suffix for the file name.
 */
final class LogFileFilter implements FileFilter {

	private final String prefix;
	private final String suffix;

	/**
	 * @param prefix
	 *            Name of a file must start with this prefix
	 * @param suffix
	 *            Name of a file must end with this suffix
	 */
	LogFileFilter(final String prefix, final String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public boolean accept(final File file) {
		String name = file.getName();
		return name.startsWith(prefix) && name.endsWith(suffix);
	}

}
