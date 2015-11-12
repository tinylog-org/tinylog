/*
 * Copyright 2014 Martin Winandy
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

import org.slf4j.helpers.NOPMDCAdapter;

/**
 * The binding for {@link org.slf4j.spi.MDCAdapter MDCAdapter}.
 *
 * @see NOPMDCAdapter
 */
public final class StaticMDCBinder {

	/**
	 * Unique instance of static MDC binder
	 */
	public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

	private static final String mdcAdapterClassName = TinylogMDCAdapter.class.getName();
	
	private final TinylogMDCAdapter adapter;

	private StaticMDCBinder() {
		adapter = new TinylogMDCAdapter();
	}

	/**
	 * Get the MDC adapter.
	 *
	 * @return MDC adapter
	 */
	public TinylogMDCAdapter getMDCA() {
		return adapter;
	}

	/**
	 * Get the class name of the MDC adapter.
	 *
	 * @return Class name of the MDC adapter
	 */
	public String getMDCAdapterClassStr() {
		return mdcAdapterClassName;
	}

}
