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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
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
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.Test;
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.labellers.ProcessIdLabeller;
import org.pmw.tinylog.labellers.TimestampLabeller;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.util.StringListOutputStream;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.LogcatWriter;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.pmw.tinylog.writers.SharedFileWriter;

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
		StringListOutputStream errorStream = getSystemErrorStream();

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.level", "TRACE"));
		assertEquals(LoggingLevel.TRACE, configuration.getLevel());

		configuration = load(new PropertiesBuilder().set("tinylog.level", "error"));
		assertEquals(LoggingLevel.ERROR, configuration.getLevel());

		assertFalse(errorStream.hasLines());
		configuration = load(new PropertiesBuilder().set("tinylog.level", "invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevel());
		assertThat(errorStream.nextLine(), allOf(containsString("invalid"), containsString("logging level")));
	}

	/**
	 * Test reading custom logging levels for packages and classes.
	 */
	@Test
	public final void testCustomLevels() {
		StringListOutputStream errorStream = getSystemErrorStream();
		PropertiesBuilder builder = new PropertiesBuilder().set("tinylog.level", "INFO");

		Configuration configuration = load(builder.set("tinylog.level@a.b", "WARNING"));
		assertEquals(LoggingLevel.WARNING, configuration.getLevel("a.b"));

		configuration = load(builder.set("tinylog.level@a.b.c", "TRACE"));
		assertEquals(LoggingLevel.TRACE, configuration.getLevel("a.b.c"));

		configuration = load(builder.set("tinylog.level@org.pmw.tinylog", "ERROR"));
		assertEquals(LoggingLevel.ERROR, configuration.getLevel("org.pmw.tinylog"));

		assertFalse(errorStream.hasLines());
		configuration = load(builder.set("tinylog.level@org.pmw.tinylog", "nonsense"));
		assertEquals(LoggingLevel.INFO, configuration.getLevel("org.pmw.tinylog"));
		assertThat(errorStream.nextLine(), allOf(containsString("nonsense"), containsString("logging level")));
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
		StringListOutputStream errorStream = getSystemErrorStream();

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "0"));
		assertEquals(0, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "1"));
		assertEquals(1, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "42"));
		assertEquals(42, configuration.getMaxStackTraceElements());

		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "-1"));
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());

		assertFalse(errorStream.hasLines());
		configuration = load(new PropertiesBuilder().set("tinylog.stacktrace", "invalid"));
		int defaultValue = Configurator.defaultConfig().create().getMaxStackTraceElements();
		assertEquals(defaultValue, configuration.getMaxStackTraceElements());
		assertThat(errorStream.nextLine(), allOf(containsString("invalid"), containsString("stack trace")));
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
		File file = FileHelper.createTemporaryFile("log");

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "file"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("file writer")));

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath()));
		assertNotNull(configuration.getWriter());
		assertEquals(FileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), ((FileWriter) configuration.getWriter()).getFilename());
		assertFalse(((FileWriter) configuration.getWriter()).isBuffered());

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.buffered", "true"));
		assertNotNull(configuration.getWriter());
		assertEquals(FileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), ((FileWriter) configuration.getWriter()).getFilename());
		assertTrue(((FileWriter) configuration.getWriter()).isBuffered());

		file.delete();
	}

	/**
	 * Test reading shared file logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testSharedFileLoggingWriter() throws IOException {
		File file = FileHelper.createTemporaryFile("log");

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "sharedfile"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("sharedfile writer")));

		configuration = load(new PropertiesBuilder().set("tinylog.writer", "sharedfile").set("tinylog.writer.filename", file.getAbsolutePath()));
		assertNotNull(configuration.getWriter());
		assertEquals(SharedFileWriter.class, configuration.getWriter().getClass());
		assertEquals(file.getAbsolutePath(), ((SharedFileWriter) configuration.getWriter()).getFilename());

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
		testRollingFileLoggingWriter(null);
		testRollingFileLoggingWriter(false);
		testRollingFileLoggingWriter(true);
	}

	/**
	 * Test reading logcat logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLogcatLoggingWriter() throws IOException {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "logcat"));
		assertNotNull(configuration.getWriter());
		assertEquals(LogcatWriter.class, configuration.getWriter().getClass());
	}

	/**
	 * Test reading an invalid logging writer.
	 */
	@Test
	public final void testInvalidLoggingWriter() {
		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "invalid"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("find"), containsString("invalid")));
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

	private void testRollingFileLoggingWriter(final Boolean buffered) throws IOException {
		File file = FileHelper.createTemporaryFile("log");
		boolean expectBuffered = Boolean.TRUE.equals(buffered);

		Configuration configuration = load(new PropertiesBuilder().set("tinylog.writer", "rollingfile"));
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("rollingfile writer")));

		PropertiesBuilder defaultProperties = new PropertiesBuilder().set("tinylog.writer", "rollingfile").set("tinylog.writer.filename",
				file.getAbsolutePath());
		if (Boolean.TRUE.equals(buffered)) {
			defaultProperties.set("tinylog.writer.buffered", "true");
		} else if (Boolean.FALSE.equals(buffered)) {
			defaultProperties.set("tinylog.writer.buffered", "false");
		}

		PropertiesBuilder properties = defaultProperties.copy();
		configuration = load(properties);
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.backups")));
		assertThat(getSystemErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("rollingfile writer")));

		properties = defaultProperties.copy().set("tinylog.writer.backups", "1");
		configuration = load(properties);
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		RollingFileWriter rollingFileWriter = (RollingFileWriter) configuration.getWriter();
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(1, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		Labeller labeller = rollingFileWriter.getLabeller();
		assertNotNull(labeller);
		assertEquals(CountLabeller.class, labeller.getClass());
		List<? extends Policy> policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(1, policies.size());
		assertEquals(StartupPolicy.class, policies.get(0).getClass());

		properties = defaultProperties.copy().set("tinylog.writer.backups", "2").set("tinylog.writer.label", "pid");
		configuration = load(properties);
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		rollingFileWriter = (RollingFileWriter) configuration.getWriter();
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(2, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeller = rollingFileWriter.getLabeller();
		assertNotNull(labeller);
		labeller.init(configuration);
		assertEquals(ProcessIdLabeller.class, labeller.getClass());
		assertEquals(new File("my." + EnvironmentHelper.getProcessId() + ".log").getAbsoluteFile(), labeller.getLogFile(new File("my.log")).getAbsoluteFile());
		policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(1, policies.size());
		assertEquals(StartupPolicy.class, policies.get(0).getClass());

		properties = defaultProperties.copy().set("tinylog.writer.backups", "3").set("tinylog.writer.label", "timestamp: yyyy")
				.set("tinylog.writer.policies", "size: 1");
		configuration = load(properties);
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		rollingFileWriter = (RollingFileWriter) configuration.getWriter();
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(3, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeller = rollingFileWriter.getLabeller();
		assertNotNull(labeller);
		assertEquals(TimestampLabeller.class, labeller.getClass());
		labeller.init(configuration);
		assertEquals(new File("my." + new SimpleDateFormat("yyyy").format(new Date()) + ".log").getAbsoluteFile(), labeller.getLogFile(new File("my.log"))
				.getAbsoluteFile());
		policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(1, policies.size());
		assertEquals(SizePolicy.class, policies.get(0).getClass());
		assertTrue(policies.get(0).check("1"));
		assertFalse(policies.get(0).check("2"));

		properties = defaultProperties.copy().set("tinylog.writer.backups", "4").set("tinylog.writer.label", "timestamp")
				.set("tinylog.writer.policies", "startup, daily");
		configuration = load(properties);
		assertNotNull(configuration.getWriter());
		assertEquals(RollingFileWriter.class, configuration.getWriter().getClass());
		rollingFileWriter = (RollingFileWriter) configuration.getWriter();
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(4, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeller = rollingFileWriter.getLabeller();
		assertNotNull(labeller);
		assertEquals(TimestampLabeller.class, labeller.getClass());
		policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(2, policies.size());
		assertEquals(StartupPolicy.class, policies.get(0).getClass());
		assertEquals(DailyPolicy.class, policies.get(1).getClass());

		file.delete();
	}

	private static Configuration load(final PropertiesBuilder propertiesBuilder) {
		return load(propertiesBuilder.create());
	}

	private static Configuration load(final Properties properties) {
		return PropertiesLoader.readProperties(properties).create();
	}

}
