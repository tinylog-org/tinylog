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

package org.tinylog.converters;

import java.util.concurrent.ThreadFactory;

/**
 * Thread factory that creates new daemon threads with a given name and lowest priority.
 */
final class NamedDaemonThreadFactory implements ThreadFactory {

	private final String name;

	/**
	 * @param name Name for created threads
	 */
	NamedDaemonThreadFactory(final String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(final Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName(name);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(true);
		return thread;
	}

}
