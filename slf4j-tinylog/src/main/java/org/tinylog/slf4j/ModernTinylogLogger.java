/*
 * Copyright 2023 Martin Winandy
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

package org.tinylog.slf4j;

import java.util.List;

import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;
import org.slf4j.spi.LoggingEventAware;
import org.tinylog.Level;

/**
 * Location and event aware logger for modern SLF4J 2.
 */
public final class ModernTinylogLogger extends AbstractTinylogLogger implements LoggingEventAware {

	/**
	 * @param name
	 *            Name for logger
	 */
	public ModernTinylogLogger(final String name) {
		super(name);
	}

	@Override
	public void log(final LoggingEvent event) {
		Level severityLevel = translateLevel(event.getLevel().toInt());
		List<Marker> markers = event.getMarkers();
		Marker marker = markers == null || markers.isEmpty() ? null : markers.get(0);
		String tag = marker == null ? null : marker.getName();

		if (provider.getMinimumLevel(tag).ordinal() <= severityLevel.ordinal()) {
			provider.log(
				event.getCallerBoundary(),
				tag,
				severityLevel,
				event.getThrowable(),
				formatter,
				event.getMessage(),
				event.getArgumentArray()
			);
		}
	}

}
