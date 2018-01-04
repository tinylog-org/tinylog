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

import java.util.Collection;
import java.util.Collections;

/**
 * Static textual segment of a path.
 */
public final class StaticSegment extends AbstractSegment {

	private final String path;

	/**
	 * @param path
	 *            Static text segment
	 * @param next
	 *            Successor path segment
	 */
	public StaticSegment(final String path, final Segment next) {
		super(next);
		this.path = path;
	}

	@Override
	public String getLatestFile(final String prefix) {
		return existDictionary(prefix + path) ? getNext().getLatestFile(prefix + path) : null;
	}

	@Override
	public Collection<String> getAllFiles(final String prefix) {
		return existDictionary(prefix + path) ? getNext().getAllFiles(prefix + path) : Collections.<String>emptyList();
	}

	@Override
	public String createNewFile(final String prefix) {
		return getNext().createNewFile(prefix + path);
	}

}
