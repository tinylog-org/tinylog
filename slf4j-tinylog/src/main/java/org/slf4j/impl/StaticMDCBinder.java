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

import org.slf4j.spi.MDCAdapter;
import org.tinylog.ThreadContext;
import org.tinylog.slf4j.TinylogMdcAdapter;

/**
 * MDC binder using tinylog's {@link ThreadContext} by SLF4J.
 */
// @checkstyle off: AbbreviationAsWordInName
public final class StaticMDCBinder {

	/**
	 * Singleton instance of this static MDC binder.
	 */
	public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

	private MDCAdapter adapter;

	/** */
	private StaticMDCBinder() {
		this.adapter = new TinylogMdcAdapter();
	}

	/**
	 * Gets the MDC adapter implementation.
	 * 
	 * @return Instance of MDC adapter
	 */
	public MDCAdapter getMDCA() {
		return adapter;
	}

	/**
	 * Gets the fully-qualified MDC adapter class name.
	 * 
	 * @return Fully-qualified class name of MDC adapter
	 */
	public String getMDCAdapterClassStr() {
		return adapter.getClass().getName();
	}

}
