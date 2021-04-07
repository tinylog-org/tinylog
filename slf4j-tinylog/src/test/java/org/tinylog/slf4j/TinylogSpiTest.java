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

	/** Find MDC adapter. */
	@Test
	public void findMdcAdapterViaSpi() {
		final MDCAdapter adapter = MDC.getMDCAdapter();

		assertThat(adapter).isExactlyInstanceOf(TinylogMdcAdapter.class);
	}

	/** Find logger factory. */
	@Test
	public void findLoggerFactoryViaSpi() {
		final ILoggerFactory factory = LoggerFactory.getILoggerFactory();

		assertThat(factory).isExactlyInstanceOf(TinylogLoggerFactory.class);
	}

	/** Find marker factory. */
	@Test
	public void findMarkerFactoryViaSpi() {
		final IMarkerFactory factory = MarkerFactory.getIMarkerFactory();

		assertThat(factory).isExactlyInstanceOf(BasicMarkerFactory.class);
	}

	/** Find logger. */
	@Test
	public void findLoggerViaSpi() {
		final Logger logger = LoggerFactory.getLogger(getClass());

		assertThat(logger).isExactlyInstanceOf(TinylogLogger.class);
	}
}
