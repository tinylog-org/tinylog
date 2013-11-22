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

import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.labellers.TimestampLabeller;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

/**
 * Test properties loader.
 * 
 * @see PropertiesLoader
 */
public class PropertiesLoaderTest extends AbstractTest {

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

		Configuration configuration = load(builder.set("tinylog.level@a.b", "WARNING"));
		assertEquals(LoggingLevel.WARNING, configuration.getLevelOfPackage("a.b"));

		configuration = load(builder.set("tinylog.level@a.b.c", "TRACE"));
		assertEquals(LoggingLevel.TRACE, configuration.getLevelOfPackage("a.b.c"));

		configuration = load(builder.set("tinylog.level@org.pmw.tinylog", "ERROR"));
		assertEquals(LoggingLevel.ERROR, configuration.getLevelOfPackage("org.pmw.tinylog"));

		configuration = load(builder.set("tinylog.level@org.pmw.tinylog", "invalid"));
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
	 * Test reading locale for message format.
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

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "42"));
		assertEquals(42, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "-1"));
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "invalid"));
		int defaultValue = Configurator.defaultConfig().create().getMaxStackTraceElements();
		assertEquals(defaultValue, configuration.getMaxStackTraceElements());
	}

	/**
	 * Test reading <code>null</code> as logging writer (no logging writer).
	 */
	@Test
	public final void testNullLoggingWriter() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "null"));
		assertNull(configuration.getWriter());
	}

	/**
	 * Test reading console logging writer.
	 */
	@Test
	public final void testConsoleLoggingWriter() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "console"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
	}

	/**
	 * Test reading file logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testFileLoggingWriter() throws IOException {
		new FileWriter(FileHelper.createTemporaryFile("log").getAbsolutePath());

		File file = FileHelper.createTemporaryFile("log");
		FileWriterMock fileWriterMock = new FileWriterMock();

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "file"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath()));
		assertNotNull(configuration.getWriter());
		assertEquals(FileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), fileWriterMock.filename);

		file.delete();
	}

	/**
	 * Test reading rolling file logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRollingFileLoggingWriter() throws IOException {
		File file = FileHelper.createTemporaryFile("log");
		RollingFileWriterMock rollingFileWriterMock = new RollingFileWriterMock();

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile").set("tinylog.writer.filename", file.getAbsolutePath()));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.backups", "1"));
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), rollingFileWriterMock.filename);
		assertEquals(1, rollingFileWriterMock.backups);
		assertNotNull(rollingFileWriterMock.labeller);
		assertEquals(CountLabeller.class, rollingFileWriterMock.labeller.getClass());
		assertNotNull(rollingFileWriterMock.policies);
		assertEquals(1, rollingFileWriterMock.policies.length);
		assertEquals(StartupPolicy.class, rollingFileWriterMock.policies[0].getClass());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.backups", "2").set("tinylog.writer.label", "timestamp: yyyy").set("tinylog.writer.policies", "size: 1"));
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), rollingFileWriterMock.filename);
		assertEquals(2, rollingFileWriterMock.backups);
		assertNotNull(rollingFileWriterMock.labeller);
		assertEquals(TimestampLabeller.class, rollingFileWriterMock.labeller.getClass());
		assertEquals(new File("my." + new SimpleDateFormat("yyyy").format(new Date()) + ".log"), rollingFileWriterMock.labeller.getLogFile(new File("my.log")));
		assertNotNull(rollingFileWriterMock.policies);
		assertEquals(1, rollingFileWriterMock.policies.length);
		assertEquals(SizePolicy.class, rollingFileWriterMock.policies[0].getClass());
		assertTrue(rollingFileWriterMock.policies[0].check(null, "1"));
		assertFalse(rollingFileWriterMock.policies[0].check(null, "2"));

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.backups", "3").set("tinylog.writer.label", "timestamp").set("tinylog.writer.policies", "startup, daily"));
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), rollingFileWriterMock.filename);
		assertEquals(3, rollingFileWriterMock.backups);
		assertNotNull(rollingFileWriterMock.labeller);
		assertEquals(TimestampLabeller.class, rollingFileWriterMock.labeller.getClass());
		assertNotNull(rollingFileWriterMock.policies);
		assertEquals(2, rollingFileWriterMock.policies.length);
		assertEquals(StartupPolicy.class, rollingFileWriterMock.policies[0].getClass());
		assertEquals(DailyPolicy.class, rollingFileWriterMock.policies[1].getClass());

		file.delete();
	}

	/**
	 * Test reading an invalid logging writer.
	 */
	@Test
	public final void testInvalidLoggingWriter() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "invalid"));
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
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "false"));
		assertNull(configuration.getWritingThread());

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.priority", "1"));
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(1, configuration.getWritingThread().getPriority());

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.observe", "null"));
		assertNotNull(configuration.getWritingThread());
		assertNull(configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configuration = load(new PropertiesBuilder().set("tinylog.writingthread", "1").set("tinylog.writingthread.observe", "null")
				.set("tinylog.writingthread.priority", "1"));
		assertNotNull(configuration.getWritingThread());
		assertNull(configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(1, configuration.getWritingThread().getPriority());
	}

	private static Configuration load(final PropertiesBuilder propertiesBuilder) {
		return load(propertiesBuilder.create());
	}

	private static Configuration load(final Properties properties) {
		return PropertiesLoader.readProperties(properties).create();
	}

	private static final class FileWriterMock extends MockUp<FileWriter> {

		private String filename;

		@Mock
		public void $init(final String filename) {
			this.filename = filename;
		}

	}

	private static final class RollingFileWriterMock extends MockUp<RollingFileWriter> {

		private String filename;
		private int backups;
		private Labeller labeller;
		private Policy[] policies;

		@Mock
		public void $init(final String filename, final int backups, final Labeller labeller, final Policy... policies) {
			this.filename = filename;
			this.backups = backups;
			this.labeller = labeller;
			this.policies = policies;
		}

	}

}
