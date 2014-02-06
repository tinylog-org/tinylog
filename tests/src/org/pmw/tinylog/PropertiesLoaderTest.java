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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.pmw.tinylog.labelers.CountLabeler;
import org.pmw.tinylog.labelers.Labeler;
import org.pmw.tinylog.labelers.ProcessIdLabeler;
import org.pmw.tinylog.labelers.TimestampLabeler;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.util.StringListOutputStream;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.LoggingWriter;
import org.pmw.tinylog.writers.PropertiesSupport;
import org.pmw.tinylog.writers.Property;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.pmw.tinylog.writers.SharedFileWriter;

/**
 * Test properties loader.
 * 
 * @see PropertiesLoader
 */
public class PropertiesLoaderTest extends AbstractTest {

	/**
	 * Test if the class is a valid utility class.
	 * 
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(PropertiesLoader.class);
	}

	/**
	 * Test read a complete configuration.
	 */
	@Test
	public final void testReadProperties() {
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.level", "warning").set("tinylog.format", "{message}")
				.set("tinylog.locale", "de").set("tinylog.stacktrace", "42").set("tinylog.writer", "null").set("tinylog.writingthread", "true");

		Configurator configurator = PropertiesLoader.readProperties(propertiesBuilder.create());
		assertNotNull(configurator);

		Configuration configuration = configurator.create();
		assertEquals(LoggingLevel.WARNING, configuration.getLevel());
		assertEquals("{message}", configuration.getFormatPattern());
		assertEquals(new Locale("de"), configuration.getLocale());
		assertNotSame(Locale.getDefault(), configuration.getLocale());
		assertEquals(42, configuration.getMaxStackTraceElements());
		assertNull(configuration.getWriter());
		assertNotNull(configuration.getWritingThread());
	}

	/**
	 * Test reading logging level.
	 */
	@Test
	public final void testReadLevel() {
		LoggingLevel defaulLevel = Configurator.defaultConfig().create().getLevel();

		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().create());
		assertEquals(defaulLevel, configurator.create().getLevel());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level", "").create());
		assertEquals(defaulLevel, configurator.create().getLevel());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level", "TRACE").create());
		assertEquals(LoggingLevel.TRACE, configurator.create().getLevel());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level", "warning").create());
		assertEquals(LoggingLevel.WARNING, configurator.create().getLevel());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level", "ErrOr").create());
		assertEquals(LoggingLevel.ERROR, configurator.create().getLevel());

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level", "invalid").create());
		assertEquals(defaulLevel, configurator.create().getLevel());
		assertThat(errorStream.nextLine(), allOf(containsString("invalid"), containsString("logging level")));
	}

	/**
	 * Test reading custom logging levels for packages and classes.
	 */
	@Test
	public final void testReadCustomLevels() {
		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level@a.b", "WARNING").create());
		assertEquals(LoggingLevel.WARNING, configurator.create().getLevel("a.b"));

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level@a.b.c", "trace").create());
		assertEquals(LoggingLevel.TRACE, configurator.create().getLevel("a.b.c"));

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level@org.pmw.tinylog", "ErrOr").create());
		assertEquals(LoggingLevel.ERROR, configurator.create().getLevel("org.pmw.tinylog"));

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLevel(configurator, new PropertiesBuilder().set("tinylog.level@org.pmw.tinylog", "nonsense").create());
		assertEquals(configurator.create().getLevel(), configurator.create().getLevel("org.pmw.tinylog"));
		assertThat(errorStream.nextLine(), allOf(containsString("nonsense"), containsString("logging level")));
	}

	/**
	 * Test reading format pattern.
	 */
	@Test
	public final void testReadFormatPattern() {
		String defaultFormatPattern = Configurator.defaultConfig().create().getFormatPattern();

		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readFormatPattern(configurator, new PropertiesBuilder().create());
		assertEquals(defaultFormatPattern, configurator.create().getFormatPattern());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readFormatPattern(configurator, new PropertiesBuilder().set("tinylog.format", "").create());
		assertEquals(defaultFormatPattern, configurator.create().getFormatPattern());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readFormatPattern(configurator, new PropertiesBuilder().set("tinylog.format", "My log entry: {message}").create());
		assertEquals("My log entry: {message}", configurator.create().getFormatPattern());
	}

	/**
	 * Test reading locale for format pattern.
	 */
	@Test
	public final void testReadLocale() {
		Locale defaultLocale = Configurator.defaultConfig().create().getLocale();

		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().create());
		assertEquals(defaultLocale, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "").create());
		assertEquals(defaultLocale, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "de").create());
		assertEquals(Locale.GERMAN, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "de_DE").create());
		assertEquals(Locale.GERMANY, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "en").create());
		assertEquals(Locale.ENGLISH, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "en_GB").create());
		assertEquals(Locale.UK, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "en_US").create());
		assertEquals(Locale.US, configurator.create().getLocale());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readLocale(configurator, new PropertiesBuilder().set("tinylog.locale", "en_US_WIN").create());
		assertEquals(new Locale("en", "US", "WIN"), configurator.create().getLocale());
	}

	/**
	 * Test reading stack trace limitation.
	 */
	@Test
	public final void testReadMaxStackTraceElements() {
		int defaultMaxStackTraceElements = Configurator.defaultConfig().create().getMaxStackTraceElements();

		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().create());
		assertEquals(defaultMaxStackTraceElements, configurator.create().getMaxStackTraceElements());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().set("tinylog.stacktrace", "").create());
		assertEquals(defaultMaxStackTraceElements, configurator.create().getMaxStackTraceElements());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().set("tinylog.stacktrace", "0").create());
		assertEquals(0, configurator.create().getMaxStackTraceElements());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().set("tinylog.stacktrace", "1").create());
		assertEquals(1, configurator.create().getMaxStackTraceElements());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().set("tinylog.stacktrace", "42").create());
		assertEquals(42, configurator.create().getMaxStackTraceElements());

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().set("tinylog.stacktrace", "-1").create());
		assertEquals(Integer.MAX_VALUE, configurator.create().getMaxStackTraceElements());

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readMaxStackTraceElements(configurator, new PropertiesBuilder().set("tinylog.stacktrace", "invalid").create());
		assertEquals(defaultMaxStackTraceElements, configurator.create().getMaxStackTraceElements());
		assertThat(errorStream.nextLine(), allOf(containsString("invalid"), containsString("stack trace")));
	}

	/**
	 * Test reading <code>null</code> as logging writer (no logging writer).
	 */
	@Test
	public final void testReadNullLoggingWriter() {
		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "null").create());
		assertNull(configurator.create().getWriter());
	}

	/**
	 * Test reading a writer with all potential properties.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadWriterWithProperties() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			PropertiesBuilder defaultPropertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties");

			/* Without any properties */

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = defaultPropertiesBuilder.copy();
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			LoggingWriter writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());

			/* With string property */

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.string", "abc");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			assertEquals("abc", ((PropertiesWriter) writer).stringValue);

			/* With integer property */

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.int", "42");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			assertEquals(Integer.valueOf(42), ((PropertiesWriter) writer).intValue);

			assertFalse(errorStream.hasLines());
			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.int", "abc");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.int"), containsString("abc")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			/* With boolean property */

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.boolean", "true");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			assertEquals(Boolean.TRUE, ((PropertiesWriter) writer).booleanValue);

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.boolean", "false");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			assertEquals(Boolean.FALSE, ((PropertiesWriter) writer).booleanValue);

			assertFalse(errorStream.hasLines());
			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.boolean", "abc");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.boolean"), containsString("abc")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			/* With labeler property */

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.labeler", "count");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			PropertiesWriter propertiesWriter = (PropertiesWriter) writer;
			assertNotNull(propertiesWriter.labeler);
			assertEquals(CountLabeler.class, propertiesWriter.labeler.getClass());

			/* With policy property */

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.policy", "startup");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			propertiesWriter = (PropertiesWriter) writer;
			assertNotNull(propertiesWriter.policy);
			assertEquals(StartupPolicy.class, propertiesWriter.policy.getClass());

			/* With policies property */

			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.policies", "startup");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(PropertiesWriter.class, writer.getClass());
			propertiesWriter = (PropertiesWriter) writer;
			assertNotNull(propertiesWriter.policies);
			assertEquals(1, propertiesWriter.policies.length);
			assertEquals(StartupPolicy.class, propertiesWriter.policies[0].getClass());
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a writer with unsupported property type.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadWriterWithUnsupportedProperties() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), ClassPropertyWriter.class.getName());
			PropertiesBuilder defaultPropertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties");

			/* Without any properties */

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = defaultPropertiesBuilder.copy();
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			LoggingWriter writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ClassPropertyWriter.class, writer.getClass());

			/* With class property */

			StringListOutputStream errorStream = getErrorStream();
			assertFalse(errorStream.hasLines());
			configurator = Configurator.defaultConfig();
			propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.class", "MyClass");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.class"), containsString("unsupported")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading console logging writer.
	 */
	@Test
	public final void testReadConsoleLoggingWriter() {
		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "console").create());
		LoggingWriter writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
	}

	/**
	 * Test reading file logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadFileLoggingWriter() throws IOException {
		StringListOutputStream errorStream = getErrorStream();
		File file = FileHelper.createTemporaryFile("log");

		assertFalse(errorStream.hasLines());
		Configurator configurator = Configurator.defaultConfig();
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "file");
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		LoggingWriter writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("file writer")));

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath());
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(FileWriter.class, writer.getClass());
		assertEquals(file.getAbsolutePath(), ((FileWriter) writer).getFilename());
		assertFalse(((FileWriter) writer).isBuffered());

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.buffered", "true");
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(FileWriter.class, writer.getClass());
		assertEquals(file.getAbsolutePath(), ((FileWriter) writer).getFilename());
		assertTrue(((FileWriter) writer).isBuffered());

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "file").set("tinylog.writer.filename", file.getAbsolutePath())
				.set("tinylog.writer.buffered", "false");
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(FileWriter.class, writer.getClass());
		assertEquals(file.getAbsolutePath(), ((FileWriter) writer).getFilename());
		assertFalse(((FileWriter) writer).isBuffered());

		file.delete();
	}

	/**
	 * Test reading shared file logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadSharedFileLoggingWriter() throws IOException {
		StringListOutputStream errorStream = getErrorStream();
		File file = FileHelper.createTemporaryFile("log");

		assertFalse(errorStream.hasLines());
		Configurator configurator = Configurator.defaultConfig();
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "sharedfile");
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		LoggingWriter writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("sharedfile writer")));

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "sharedfile").set("tinylog.writer.filename", file.getAbsolutePath());
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(SharedFileWriter.class, writer.getClass());
		assertEquals(file.getAbsolutePath(), ((SharedFileWriter) writer).getFilename());

		file.delete();

	}

	/**
	 * Test reading rolling file logging writer.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadRollingFileLoggingWriter() throws IOException {
		StringListOutputStream errorStream = getErrorStream();
		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "rollingfile").create());
		LoggingWriter writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("rollingfile writer")));

		testReadRollingFileLoggingWriter(null); // Default
		testReadRollingFileLoggingWriter(false); // Non buffered
		testReadRollingFileLoggingWriter(true); // Buffered
	}

	/**
	 * Test reading a nonexistent logging writer.
	 */
	@Test
	public final void testReadInvalidLoggingWriter() {
		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "invalid").create());
		LoggingWriter writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
		assertThat(getErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("invalid"), containsString("writer")));
	}

	/**
	 * Test reading a nonexistent labeler.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadInvalidLabeler() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", "invalid");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("invalid"), containsString("labeler")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a nonexistent policy.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadInvalidPolicy() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", "invalid");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("invalid"), containsString("policy")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			configurator = Configurator.defaultConfig();
			propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policies", "invalid");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("invalid"), containsString("policy")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a writer if there is no file with registered logging writers.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadWriterIfNoRegistered() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), (String) null);

			StringListOutputStream errorStream = getErrorStream();
			Configurator configurator = Configurator.defaultConfig();
			PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "console").create());
			LoggingWriter writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("console"), containsString("writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a labeler if there is no file with registered labelers.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadLabelerIfNoRegistered() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			mock.set("META-INF/services/" + Labeler.class.getPackage().getName(), (String) null);

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", "count");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("find"), containsString("count"), containsString("labeler")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a policy if there is no file with registered policies.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadPolicyIfNoRegistered() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			mock.set("META-INF/services/" + Policy.class.getPackage().getName(), (String) null);

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", "startup");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("find"), containsString("startup"), containsString("policy")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			configurator = Configurator.defaultConfig();
			propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policies", "startup");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("find"), containsString("startup"), containsString("policy")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a writer if failed to open and read the file with registered logging writers.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadWriterIfFailedReadingServiceFile() throws IOException {
		MockUp<BufferedReader> mock = new MockUp<BufferedReader>() {

			@Mock
			public String readLine() throws IOException {
				throw new IOException();
			}

		};

		try {
			StringListOutputStream errorStream = getErrorStream();
			Configurator configurator = Configurator.defaultConfig();
			PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "console").create());
			LoggingWriter writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString("read"), containsString("services")));
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("console"), containsString("writer")));
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test reading a registered writer but the class is missing.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadWriterWithMissingClass() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), "a.b.c.MyWriter");

			StringListOutputStream errorStream = getErrorStream();
			Configurator configurator = Configurator.defaultConfig();
			PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "mywriter").create());
			LoggingWriter writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("class"), containsString("a.b.c.MyWriter")));
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("writer"), containsString("mywriter")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a registered labeler but the class is missing.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadLabelerWithMissingClass() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			mock.set("META-INF/services/" + Labeler.class.getPackage().getName(), "a.b.c.MyLabeler");

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", "mylabeler");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("class"), containsString("a.b.c.MyLabeler")));
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("labeler"), containsString("mylabeler")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test reading a registered policy but the class is missing.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadPolicyWithMissingClass() throws IOException {
		ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			mock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			mock.set("META-INF/services/" + Policy.class.getPackage().getName(), "a.b.c.MyPolicy");

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", "mypolicy");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("class"), containsString("a.b.c.MyPolicy")));
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("policy"), containsString("mypolicy")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			configurator = Configurator.defaultConfig();
			propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policies", "mypolicy");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("class"), containsString("a.b.c.MyPolicy")));
			assertThat(errorStream.nextLine(), allOf(containsString("find"), containsString("policy"), containsString("mypolicy")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
		} finally {
			mock.tearDown();
			mock.close();
		}
	}

	/**
	 * Test potential exception while instantiation of writers.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadWriterIfInstantiationFailed() throws IOException {
		ClassLoaderMock classLoaderMock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			classLoaderMock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), EvilWriter.class.getName());

			Configurator configurator = Configurator.defaultConfig();
			PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "evil").create());
			LoggingWriter writer = configurator.create().getWriter();
			assertNotNull(writer);
			assertEquals(ConsoleWriter.class, writer.getClass());
			assertThat(errorStream.nextLine(), allOf(containsString(EvilWriter.class.getName()), containsString(UnsupportedOperationException.class.getName())));
			assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("writer"), containsString("evil")));

			for (final Exception exception : Arrays.asList(new IllegalArgumentException(), new InstantiationException(), new IllegalAccessException())) {
				MockUp<Constructor<?>> mock = new MockUp<Constructor<?>>() {

					@Mock
					public Object newInstance(final Object... arguments) throws Exception {
						throw exception;
					}

				};
				try {
					configurator = Configurator.defaultConfig();
					PropertiesLoader.readWriter(configurator, new PropertiesBuilder().set("tinylog.writer", "evil").create());
					writer = configurator.create().getWriter();
					assertNotNull(writer);
					assertEquals(ConsoleWriter.class, writer.getClass());
					assertThat(errorStream.nextLine(), allOf(containsString(EvilWriter.class.getName()), containsString(exception.getClass().getName())));
					assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("writer"), containsString("evil")));
				} finally {
					mock.tearDown();
				}
			}
		} finally {
			classLoaderMock.tearDown();
			classLoaderMock.close();
		}
	}

	/**
	 * Test potential exception while instantiation of labelers.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadLabelerIfInstantiationFailed() throws IOException {
		ClassLoaderMock classLoaderMock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			classLoaderMock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			classLoaderMock.set("META-INF/services/" + Labeler.class.getPackage().getName(), EvilLabeler.class.getName());

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", "evil");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(),
					allOf(containsString(EvilLabeler.class.getName()), containsString(UnsupportedOperationException.class.getName())));
			assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("labeler"), containsString("evil")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			configurator = Configurator.defaultConfig();
			propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", "evil: abc");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString(EvilLabeler.class.getName()), containsString(NoSuchMethodException.class.getName())));
			assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("labeler"), containsString("evil")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			for (final Exception exception : Arrays.asList(new InstantiationException(), new IllegalAccessException(), new IllegalArgumentException())) {
				MockUp<Class<?>> mock = new MockUp<Class<?>>() {

					@Mock
					public Constructor<?> getDeclaredConstructor(final Class<?>... parameterTypes) throws Exception {
						throw exception;
					}

				};
				try {
					configurator = Configurator.defaultConfig();
					propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", "evil");
					PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
					assertThat(errorStream.nextLine(), allOf(containsString(EvilLabeler.class.getName()), containsString(exception.getClass().getName())));
					assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("labeler"), containsString("evil")));
					assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
				} finally {
					mock.tearDown();
				}
			}
		} finally {
			classLoaderMock.tearDown();
			classLoaderMock.close();
		}
	}

	/**
	 * Test potential exception while instantiation of policies.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testReadPolicyIfInstantiationFailed() throws IOException {
		ClassLoaderMock classLoaderMock = new ClassLoaderMock((URLClassLoader) PropertiesLoader.class.getClassLoader());
		try {
			StringListOutputStream errorStream = getErrorStream();
			classLoaderMock.set("META-INF/services/" + LoggingWriter.class.getPackage().getName(), PropertiesWriter.class.getName());
			classLoaderMock.set("META-INF/services/" + Policy.class.getPackage().getName(), EvilPolicy.class.getName());

			Configurator configurator = Configurator.defaultConfig();
			PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", "evil");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString(EvilPolicy.class.getName()), containsString(UnsupportedOperationException.class.getName())));
			assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("policy"), containsString("evil")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			configurator = Configurator.defaultConfig();
			propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", "evil: abc");
			PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
			assertThat(errorStream.nextLine(), allOf(containsString(PropertiesLoader.class.getName()), containsString(NoSuchMethodException.class.getName())));
			assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("policy"), containsString("evil")));
			assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));

			for (final Exception exception : Arrays.asList(new InstantiationException(), new IllegalAccessException(), new IllegalArgumentException())) {
				MockUp<Class<?>> mock = new MockUp<Class<?>>() {

					@Mock
					public Constructor<?> getDeclaredConstructor(final Class<?>... parameterTypes) throws Exception {
						throw exception;
					}

				};
				try {
					configurator = Configurator.defaultConfig();
					propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", "evil");
					PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
					assertThat(errorStream.nextLine(), allOf(containsString(EvilPolicy.class.getName()), containsString(exception.getClass().getName())));
					assertThat(errorStream.nextLine(), allOf(containsString("initialize"), containsString("policy"), containsString("evil")));
					assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("properties writer")));
				} finally {
					mock.tearDown();
				}
			}
		} finally {
			classLoaderMock.tearDown();
			classLoaderMock.close();
		}
	}

	/**
	 * Test reading writing thread.
	 */
	@Test
	public final void testReadWritingThread() {
		Configurator configurator = Configurator.defaultConfig();
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		Configuration configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "TRUE");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configurator = Configurator.defaultConfig();
		PropertiesLoader.readWritingThread(configurator, new PropertiesBuilder().set("tinylog.writingthread", "false").create());
		configuration = configurator.create();
		assertNull(configuration.getWritingThread());

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.priority", "1");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(1, configuration.getWritingThread().getPriority());

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.priority", "9");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(9, configuration.getWritingThread().getPriority());

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.priority", "invalid");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));
		assertThat(errorStream.nextLine(), allOf(containsString("invalid"), containsString("priority")));

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.observe", "null");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertNull(configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configurator = Configurator.defaultConfig();
		String threadName = Thread.currentThread().getName();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.observe", threadName);
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals(threadName, configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configurator = Configurator.defaultConfig();
		propertiesBuilder = new PropertiesBuilder().set("tinylog.writingthread", "true").set("tinylog.writingthread.observe", "null")
				.set("tinylog.writingthread.priority", "1");
		PropertiesLoader.readWritingThread(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertNull(configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(1, configuration.getWritingThread().getPriority());
	}

	private void testReadRollingFileLoggingWriter(final Boolean buffered) throws IOException {
		File file = FileHelper.createTemporaryFile("log");
		boolean expectBuffered = Boolean.TRUE.equals(buffered);

		PropertiesBuilder defaultPropertiesBuilder = new PropertiesBuilder();
		defaultPropertiesBuilder.set("tinylog.writer", "rollingfile");
		defaultPropertiesBuilder.set("tinylog.writer.filename", file.getAbsolutePath());
		if (Boolean.TRUE.equals(buffered)) {
			defaultPropertiesBuilder.set("tinylog.writer.buffered", "true");
		} else if (Boolean.FALSE.equals(buffered)) {
			defaultPropertiesBuilder.set("tinylog.writer.buffered", "false");
		}

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		PropertiesBuilder propertiesBuilder = defaultPropertiesBuilder.copy();
		Configurator configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		LoggingWriter writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.backups")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("rollingfile writer")));

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "1");
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(RollingFileWriter.class, writer.getClass());
		RollingFileWriter rollingFileWriter = (RollingFileWriter) writer;
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(1, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		Labeler labeler = rollingFileWriter.getLabeler();
		assertNotNull(labeler);
		assertEquals(CountLabeler.class, labeler.getClass());
		List<? extends Policy> policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(1, policies.size());
		assertEquals(StartupPolicy.class, policies.get(0).getClass());

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "2").set("tinylog.writer.label", "pid");
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		Configuration configuration = configurator.create();
		writer = configuration.getWriter();
		assertNotNull(writer);
		assertEquals(RollingFileWriter.class, writer.getClass());
		rollingFileWriter = (RollingFileWriter) writer;
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(2, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertNotNull(labeler);
		labeler.init(configuration);
		assertEquals(ProcessIdLabeler.class, labeler.getClass());
		assertEquals(new File("my." + EnvironmentHelper.getProcessId() + ".log").getAbsoluteFile(), labeler.getLogFile(new File("my.log")).getAbsoluteFile());
		policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(1, policies.size());
		assertEquals(StartupPolicy.class, policies.get(0).getClass());

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "3").set("tinylog.writer.label", "timestamp: yyyy")
				.set("tinylog.writer.policies", "size: 1");
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		configuration = configurator.create();
		writer = configuration.getWriter();
		assertNotNull(writer);
		assertEquals(RollingFileWriter.class, writer.getClass());
		rollingFileWriter = (RollingFileWriter) writer;
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(3, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertNotNull(labeler);
		assertEquals(TimestampLabeler.class, labeler.getClass());
		labeler.init(configuration);
		assertEquals(new File("my." + new SimpleDateFormat("yyyy").format(new Date()) + ".log").getAbsoluteFile(), labeler.getLogFile(new File("my.log"))
				.getAbsoluteFile());
		policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(1, policies.size());
		assertEquals(SizePolicy.class, policies.get(0).getClass());
		assertTrue(policies.get(0).check("1"));
		assertFalse(policies.get(0).check("2"));

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "4").set("tinylog.writer.label", "timestamp")
				.set("tinylog.writer.policies", "startup, daily");
		configurator = Configurator.defaultConfig();
		PropertiesLoader.readWriter(configurator, propertiesBuilder.create());
		writer = configurator.create().getWriter();
		assertNotNull(writer);
		assertEquals(RollingFileWriter.class, writer.getClass());
		rollingFileWriter = (RollingFileWriter) writer;
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(4, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertNotNull(labeler);
		assertEquals(TimestampLabeler.class, labeler.getClass());
		policies = rollingFileWriter.getPolicies();
		assertNotNull(policies);
		assertEquals(2, policies.size());
		assertEquals(StartupPolicy.class, policies.get(0).getClass());
		assertEquals(DailyPolicy.class, policies.get(1).getClass());

		file.delete();
	}

	@PropertiesSupport(name = "properties", properties = { @Property(name = "string", type = String.class, optional = true),
			@Property(name = "int", type = int.class, optional = true), @Property(name = "boolean", type = boolean.class, optional = true),
			@Property(name = "labeler", type = Labeler.class, optional = true), @Property(name = "policy", type = Policy.class, optional = true),
			@Property(name = "policies", type = Policy[].class, optional = true) })
	private static final class PropertiesWriter extends NullWriter {

		private final String stringValue;
		private final Integer intValue;
		private final Boolean booleanValue;
		private final Labeler labeler;
		private final Policy policy;
		private final Policy[] policies;

		@SuppressWarnings("unused")
		public PropertiesWriter(final String stringValue, final Labeler labeler, final Policy policy, final Policy[] policies) {
			this.stringValue = stringValue;
			this.intValue = null;
			this.booleanValue = null;
			this.labeler = labeler;
			this.policy = policy;
			this.policies = policies;
		}

		@SuppressWarnings("unused")
		public PropertiesWriter(final String stringValue, final int intValue, final Labeler labeler, final Policy policy, final Policy[] policies) {
			this.stringValue = stringValue;
			this.intValue = intValue;
			this.booleanValue = null;
			this.labeler = labeler;
			this.policy = policy;
			this.policies = policies;
		}

		@SuppressWarnings("unused")
		public PropertiesWriter(final String stringValue, final boolean booleanValue, final Labeler labeler, final Policy policy, final Policy[] policies) {
			this.stringValue = stringValue;
			this.intValue = null;
			this.booleanValue = booleanValue;
			this.labeler = labeler;
			this.policy = policy;
			this.policies = policies;
		}

		@SuppressWarnings("unused")
		public PropertiesWriter(final String stringValue, final int intValue, final boolean booleanValue, final Labeler labeler, final Policy policy,
				final Policy[] policies) {
			this.stringValue = stringValue;
			this.intValue = intValue;
			this.booleanValue = booleanValue;
			this.labeler = labeler;
			this.policy = policy;
			this.policies = policies;
		}

	}

	@PropertiesSupport(name = "properties", properties = { @Property(name = "class", type = Class.class, optional = true) })
	private static final class ClassPropertyWriter extends NullWriter {

		@SuppressWarnings("unused")
		public ClassPropertyWriter(final Class<?> clazz) {
		}

	}

	@PropertiesSupport(name = "evil", properties = { })
	private static final class EvilWriter extends NullWriter {

		@SuppressWarnings("unused")
		public EvilWriter() throws Exception {
			throw new UnsupportedOperationException();
		}

	}

	@org.pmw.tinylog.labelers.PropertiesSupport(name = "evil")
	private static final class EvilLabeler implements Labeler {

		@SuppressWarnings("unused")
		public EvilLabeler() throws Exception {
			throw new UnsupportedOperationException();
		}

		@Override
		public void init(final Configuration configuration) {
			throw new UnsupportedOperationException();
		}

		@Override
		public File getLogFile(final File baseFile) {
			throw new UnsupportedOperationException();
		}

		@Override
		public File roll(final File file, final int maxBackups) {
			throw new UnsupportedOperationException();
		}

	}

	@org.pmw.tinylog.policies.PropertiesSupport(name = "evil")
	private static final class EvilPolicy implements Policy {

		@SuppressWarnings("unused")
		public EvilPolicy() throws Exception {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean initCheck(final File logFile) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean check(final String logEntry) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void reset() {
			throw new UnsupportedOperationException();
		}

	}

}
