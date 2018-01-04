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
import java.util.Collection;
import java.util.Collections;

/**
 * Last segment of a path without a further successor.
 */
final class FinalSegment implements Segment {

	static final FinalSegment INSTANCE = new FinalSegment();

	/** */
	private FinalSegment() {
	}

	@Override
	public String getLatestFile(final String path) {
		return new File(path).isFile() ? path : null;
	}

	@Override
	public Collection<String> getAllFiles(final String path) {
		return new File(path).isFile() ? Collections.<String>singleton(path) : Collections.<String>emptyList();
	}

	@Override
	public String createNewFile(final String path) {
		return path;
	}

}
