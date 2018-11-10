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

package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * Marker factory binder for using SLF4J's {@link BasicMarker}. These markers will be translated to tags for tinylog.
 */
public final class StaticMarkerBinder implements MarkerFactoryBinder {

	/**
	 * Singleton instance of this static marker binder.
	 */
	public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

	private final IMarkerFactory factory;

	/** */
	private StaticMarkerBinder() {
		factory = new BasicMarkerFactory();
	}

	@Override
	public IMarkerFactory getMarkerFactory() {
		return factory;
	}

	@Override
	public String getMarkerFactoryClassStr() {
		return factory.getClass().getName();
	}

}
