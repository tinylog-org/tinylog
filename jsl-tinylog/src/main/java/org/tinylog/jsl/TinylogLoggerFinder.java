/*
 * Copyright 2021 Gerrit Rode
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

package org.tinylog.jsl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Logger finder for Java System.Logger that uses tinylog as back-end.
 */
public class TinylogLoggerFinder extends System.LoggerFinder {

	private final ConcurrentMap<String, TinylogLogger> loggers = new ConcurrentHashMap<>();

	public TinylogLoggerFinder() {
		super();
	}

	@Override
	public System.Logger getLogger(final String name, final Module module) {
		return loggers.computeIfAbsent(name, TinylogLogger::new);
	}
}
