/*
 * Copyright 2021 Martin Winandy
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

import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Tinylog discovery via the service provider interface (SPI).
 */
public class TinylogSpiTest {

	/**
	 * Verifies that the MDC adapter can be found and loaded.
	 */
	@Test
	public void findMdcAdapterViaSpi() {
		MDCAdapter adapter = MDC.getMDCAdapter();
		assertThat(adapter).isExactlyInstanceOf(TinylogMdcAdapter.class);
	}

	/**
	 * Verifies that the logger factory can be found and loaded.
	 */
	@Test
	public void findLoggerFactoryViaSpi() {
		ILoggerFactory factory = LoggerFactory.getILoggerFactory();
		assertThat(factory).isExactlyInstanceOf(ModernTinylogLoggerFactory.class);
	}

	/**
	 * Verifies that the marker factory can be found and loaded.
	 */
	@Test
	public void findMarkerFactoryViaSpi() {
		IMarkerFactory factory = MarkerFactory.getIMarkerFactory();
		assertThat(factory).isExactlyInstanceOf(BasicMarkerFactory.class);
	}

	/**
	 * Verifies that logger instances can be provided.
	 */
	@Test
	public void findLoggerViaSpi() {
		Logger logger = LoggerFactory.getLogger(getClass());
		assertThat(logger).isInstanceOf(ModernTinylogLogger.class);
	}

}
