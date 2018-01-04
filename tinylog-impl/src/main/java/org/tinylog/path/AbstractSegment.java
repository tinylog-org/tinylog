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

/**
 * Base class for path segments.
 */
public abstract class AbstractSegment implements Segment {

	private final Segment next;

	/**
	 * @param next
	 *            Successor if available ({@code null} will be automatically replaced with
	 *            {@link FinalSegment#INSTANCE})
	 */
	public AbstractSegment(final Segment next) {
		this.next = next == null ? FinalSegment.INSTANCE : next;
	}

	/**
	 * Gets the successor.
	 * 
	 * @return Successor or {@link FinalSegment#INSTANCE}
	 */
	public Segment getNext() {
		return next;
	}

	/**
	 * Gets the directory of a potential incomplete path.
	 * 
	 * @param path
	 *            Path that can be incomplete
	 * @return Path of directory
	 */
	protected static String getDictionary(final String path) {
		int split = Math.max(path.lastIndexOf(File.separatorChar), path.lastIndexOf('/'));
		return split == -1 ? "" : path.substring(0, split + 1);
	}

	/**
	 * Checks if a directory of a potential incomplete path exists.
	 * 
	 * @param path
	 *            Path that can be incomplete
	 * @return {@code true} if directory exists, {@code false} if not
	 */
	protected static boolean existDictionary(final String path) {
		return new File(getDictionary(path)).isDirectory();
	}

}
