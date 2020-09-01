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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for sorting files by last modification date. The most recently modified file tuples come first, the oldest
 * last.
 */
final class LastModifiedFileTupleComparator implements Comparator<FileTuple>, Serializable {

	static final LastModifiedFileTupleComparator INSTANCE = new LastModifiedFileTupleComparator();

	private static final long serialVersionUID = 1L;

	/** */
	private LastModifiedFileTupleComparator() {
	}

	@Override
	public int compare(final FileTuple first, final FileTuple second) {
		long firstModificationDate = first.getLastModified();
		long secondModificationDate = second.getLastModified();
		return (firstModificationDate < secondModificationDate) ? +1 : ((firstModificationDate == secondModificationDate) ? 0 : -1);
	}

}
