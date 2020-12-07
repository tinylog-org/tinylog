/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.format;

import java.util.Iterator;

/**
 * Endless iterator, which returns the same static element for all {@link #next()} calls. Since the method
 * {@link #hasNext()} never returns {@code false}, this iterator never terminates.
 *
 * @param <E> Type of element to store and return
 */
class EndlessIterator<E> implements Iterator<E> {

	private final E value;

	/**
	 * @param value Value to return infinitely as next element for {@link #next()}
	 */
	EndlessIterator(final E value) {
		this.value = value;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public E next() {
		return value;
	}

}
