/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.State;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for configuration.
 * 
 * @see Configuration
 */
public class ConfigurationTest extends AbstractTest {

	/**
	 * Test a minimal configuration sample.
	 */
	@Test
	public final void testMinimalConfigurationSample() {
		Configuration configuration = createMinimalConfigurationSample();
		testMinimalConfigurationSample(configuration);
	}

	/**
	 * Test a detailed configuration sample.
	 */
	@Test
	public final void testDetailedConfigurationSample() {
		Configuration configuration = createDetailedConfigurationSample();
		testDetailedConfigurationSample(configuration);
	}

	/**
	 * Test copying.
	 */
	@Test
	public final void testCopy() {
		Configuration minimalConfiguration = createMinimalConfigurationSample();
		Configuration minimalCopy = minimalConfiguration.copy().create();
		assertNotSame(minimalConfiguration, minimalCopy);
		testMinimalConfigurationSample(minimalCopy);

		Configuration detailedConfiguration = createDetailedConfigurationSample();
		Configuration detailedCopy = detailedConfiguration.copy().create();
		assertNotSame(detailedConfiguration, detailedCopy);
		testDetailedConfigurationSample(detailedCopy);
	}

	private Configuration createMinimalConfigurationSample() {
		return new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, null, null, 0);
	}

	private void testMinimalConfigurationSample(final Configuration configuration) {
		assertEquals(LoggingLevel.TRACE, configuration.getLevel());
		assertEquals(LoggingLevel.OFF, configuration.getLowestPackageLevel());
		assertEquals(LoggingLevel.TRACE, configuration.getLevelOfClass(ConfigurationTest.class.getName()));
		assertEquals(LoggingLevel.TRACE, configuration.getLevelOfPackage(ConfigurationTest.class.getPackage().getName()));
		assertEquals("", configuration.getFormatPattern());
		assertEquals(Collections.emptyList(), configuration.getFormatTokens());
		assertEquals(Locale.ROOT, configuration.getLocale());
		assertNull(configuration.getWriter());
		assertNull(configuration.getWritingThread());
		assertEquals(0, configuration.getMaxStackTraceElements());
		assertFalse(configuration.isFullStackTraceElemetRequired());
	}

	private Configuration createDetailedConfigurationSample() {
		Map<String, LoggingLevel> packageLevels = new HashMap<String, LoggingLevel>();
		packageLevels.put(ConfigurationTest.class.getPackage().getName(), LoggingLevel.INFO);
		return new Configuration(LoggingLevel.WARNING, packageLevels, "{class}{method}", Locale.GERMANY, new StoreWriter(), new WritingThread(null,
				Thread.MIN_PRIORITY), Integer.MAX_VALUE);
	}

	private void testDetailedConfigurationSample(final Configuration configuration) {
		assertEquals(LoggingLevel.WARNING, configuration.getLevel());
		assertEquals(LoggingLevel.INFO, configuration.getLowestPackageLevel());
		assertEquals(LoggingLevel.WARNING, configuration.getLevelOfClass("invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevelOfClass(ConfigurationTest.class.getName()));
		assertEquals(LoggingLevel.WARNING, configuration.getLevelOfPackage("invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevelOfPackage(ConfigurationTest.class.getPackage().getName()));
		assertEquals("{class}{method}", configuration.getFormatPattern());
		assertEquals(2, configuration.getFormatTokens().size());
		assertEquals(Locale.GERMANY, configuration.getLocale());
		assertNotNull(configuration.getWriter());
		assertEquals(StoreWriter.class, configuration.getWriter().getClass());
		assertNotNull(configuration.getWritingThread());
		assertEquals(State.NEW, configuration.getWritingThread().getState());
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());
		assertTrue(configuration.isFullStackTraceElemetRequired());
	}

}
