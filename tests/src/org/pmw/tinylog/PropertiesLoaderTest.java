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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.junit.Test;
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.TimestampLabeller;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.HourlyPolicy;
import org.pmw.tinylog.policies.MonthlyPolicy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.policies.WeeklyPolicy;
import org.pmw.tinylog.policies.YearlyPolicy;
import org.pmw.tinylog.util.LabellerWriter;
import org.pmw.tinylog.util.PolicyWriter;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

/**
 * Test reading properties for the logger.
 * 
 * @see PropertiesLoader
 */
public class PropertiesLoaderTest extends AbstractTest {

	private static final int DEFAULT_MAX_STACK_TRACE_ELEMENTS = 40;
	private static final String DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD = "main";
	private static final int DEFAULT_PRIORITY_FOR_WRITING_THREAD = 2;

	/**
	 * Test reading logging level.
	 */
	@Test
	public final void testLevel() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.level", "TRACE"));
		assertEquals(LoggingLevel.TRACE, configuration.getLevel());

		configuration = load(new PropertiesBuilder().set("tinylog.level", "error"));
		assertEquals(LoggingLevel.ERROR, configuration.getLevel());

		configuration = load(new PropertiesBuilder().set("tinylog.level", "invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevel());
	}

	/**
	 * Test reading special logging levels for packages.
	 */
	@Test
	public final void testPackageLevels() {
		PropertiesBuilder builder = new PropertiesBuilder().set("tinylog.level", "INFO");

		Configuration configuration = load(builder.set("tinylog.level:a.b", "WARNING"));
		assertEquals(LoggingLevel.WARNING, configuration.getLevelOfPackage("a.b"));

		configuration = load(builder.set("tinylog.level:a.b.c", "TRACE"));
		assertEquals(LoggingLevel.TRACE, configuration.getLevelOfPackage("a.b.c"));

		configuration = load(builder.set("tinylog.level:org.pmw.tinylog", "ERROR"));
		assertEquals(LoggingLevel.ERROR, configuration.getLevelOfPackage("org.pmw.tinylog"));

		configuration = load(builder.set("tinylog.level:org.pmw.tinylog", "invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevelOfPackage("org.pmw.tinylog"));
	}

	/**
	 * Test reading logging format.
	 */
	@Test
	public final void testFormat() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.format", "My log entry: {message}"));
		assertEquals("My log entry: {message}", configuration.getFormatPattern());
	}

	/**
	 * Test locale for message format.
	 */
	@Test
	public final void testLocale() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.locale", "de"));
		assertEquals(Locale.GERMAN, configuration.getLocale());

		configuration = load(new PropertiesBuilder().set("tinylog.locale", "de_DE"));
		assertEquals(Locale.GERMANY, configuration.getLocale());

		configuration = load(new PropertiesBuilder().set("tinylog.locale", "en"));
		assertEquals(Locale.ENGLISH, configuration.getLocale());

		configuration = load(new PropertiesBuilder().set("tinylog.locale", "en_GB"));
		assertEquals(Locale.UK, configuration.getLocale());

		configuration = load(new PropertiesBuilder().set("tinylog.locale", "en_US"));
		assertEquals(Locale.US, configuration.getLocale());

		configuration = load(new PropertiesBuilder().set("tinylog.locale", "en_US_WIN"));
		assertEquals(new Locale("en", "US", "WIN"), configuration.getLocale());
	}

	/**
	 * Test reading stack trace limitation.
	 */
	@Test
	public final void testStackTrace() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "0"));
		assertEquals(0, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "1"));
		assertEquals(1, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "5"));
		assertEquals(5, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "-1"));
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "invalid"));
		assertEquals(DEFAULT_MAX_STACK_TRACE_ELEMENTS, configuration.getMaxStackTraceElements());
	}

	/**
	 * Test reading logging writer.
	 * 
	 * @throws IOException
	 *             Failed to create temp file
	 */
	@Test
	public final void testLoggingWriter() throws IOException {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "null"));
		assertNull(configuration.getWriter());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "console"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "null"));
		assertNull(configuration.getWriter());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "file"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());

		File file = File.createTempFile("test", "tmp");
		file.deleteOnExit();
		configuration = load(new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath()));
		assertNotNull(configuration.getWriter());
		assertEquals(FileWriter.class, configuration.getWriter().getClass());
		file.delete();

		file = File.createTempFile("test", "tmp");
		file.deleteOnExit();
		configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.backups", "0"));
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		file.delete();

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "invalid"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
	}

	/**
	 * Test reading writing thread.
	 */
	@Test
	public final void testWritingThread() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "true"));
		assertNotNull(configuration.getWritingThread());
		assertEquals(DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(DEFAULT_PRIORITY_FOR_WRITING_THREAD, configuration.getWritingThread().getPriority());

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "false"));
		assertNull(configuration.getWritingThread());

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.priority", "1"));
		assertNotNull(configuration.getWritingThread());
		assertEquals(DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(1, configuration.getWritingThread().getPriority());

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.observe", "null"));
		assertNotNull(configuration.getWritingThread());
		assertNull(configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(DEFAULT_PRIORITY_FOR_WRITING_THREAD, configuration.getWritingThread().getPriority());

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "1").set("tinylog.writingthread.observe", "null")
				.set("tinylog.writingthread.priority", "1"));
		assertNotNull(configuration.getWritingThread());
		assertNull(configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(1, configuration.getWritingThread().getPriority());
	}

	/**
	 * Test reading policies.
	 */
	@Test
	public final void testLoadPolicies() {
		PropertiesBuilder builder = new PropertiesBuilder().set("tinylog.writer", "policy").set("tinylog.writer.ignored", "true");

		Configuration configuration = load(builder.set("tinylog.writer.policy", "startup"));
		PolicyWriter writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(StartupPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "startup"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(StartupPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "size: 1MB"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(SizePolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "hourly"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(HourlyPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "daily: 24:00"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(DailyPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "weekly: monday"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(WeeklyPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "monthly"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(MonthlyPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "yearly: january"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(YearlyPolicy.class, writer.getPolicies().get(0).getClass());

		configuration = load(builder.set("tinylog.writer.policy", "invalid"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(0, writer.getPolicies().size());

		builder.clear("tinylog.writer.ignored");

		configuration = load(builder.set("tinylog.writer.policies", "startup, daily: 00:00"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(2, writer.getPolicies().size());
		assertEquals(StartupPolicy.class, writer.getPolicies().get(0).getClass());
		assertEquals(DailyPolicy.class, writer.getPolicies().get(1).getClass());

		configuration = load(builder.set("tinylog.writer.policies", "invalid"));
		writer = (PolicyWriter) configuration.getWriter();
		assertNotNull(writer);
		assertEquals(0, writer.getPolicies().size());
	}

	/**
	 * Test reading labellers.
	 */
	@Test
	public final void testLoadLabellers() {
		PropertiesBuilder builder = new PropertiesBuilder().set("tinylog.writer", "labeller");

		Configuration configuration = load(builder.set("tinylog.writer.labeling", "count"));
		LabellerWriter writer = (LabellerWriter) configuration.getWriter();
		assertNotNull(writer);
		assertNotNull(writer.getLabeller());
		assertEquals(CountLabeller.class, writer.getLabeller().getClass());

		configuration = load(builder.set("tinylog.writer.labeling", "timestamp"));
		writer = (LabellerWriter) configuration.getWriter();
		assertNotNull(writer);
		assertNotNull(writer.getLabeller());
		assertEquals(TimestampLabeller.class, writer.getLabeller().getClass());

		configuration = load(builder.set("tinylog.writer.labeling", "timestamp: yyyyMMdd"));
		writer = (LabellerWriter) configuration.getWriter();
		assertNotNull(writer);
		assertNotNull(writer.getLabeller());
		assertEquals(TimestampLabeller.class, writer.getLabeller().getClass());

		configuration = load(builder.set("tinylog.writer.labeling", "invalid"));
		writer = (LabellerWriter) configuration.getWriter();
		assertNotNull(writer);
		assertNull(writer.getLabeller());
	}

	private static Configuration load(final PropertiesBuilder propertiesBuilder) {
		return load(propertiesBuilder.getProperties());
	}

	private static Configuration load(final Properties properties) {
		return PropertiesLoader.readProperties(properties).create();
	}

	private static final class PropertiesBuilder {

		private final Properties properties = new Properties();

		private PropertiesBuilder set(final String key, final String value) {
			properties.setProperty(key, value);
			return this;
		}

		private PropertiesBuilder clear(final String key) {
			properties.remove(key);
			return this;
		}

		private Properties getProperties() {
			return properties;
		}

	}

}
