package org.pmw.tinylog.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Test;
import org.pmw.tinylog.ConsoleLoggingWriter;
import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.FileLoggingWriter;
import org.pmw.tinylog.ILoggingWriter;
import org.pmw.tinylog.Logger;

/**
 * Test reading of properties for the logger.
 * 
 * @see org.pmw.tinylog.Logger
 */
public class PropertiesTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Clear properties.
	 */
	@After
	public final void init() {
		System.clearProperty("tinylog.level");
		System.clearProperty("tinylog.format");
		System.clearProperty("tinylog.stacktrace");
		System.clearProperty("tinylog.writer");
		System.clearProperty("tinylog.writer.file");
	}

	/**
	 * Test reading logging level.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testLevel() throws Exception {
		System.setProperty("tinylog.level", "TRACE");
		readProperties();
		assertEquals(ELoggingLevel.TRACE, Logger.getLoggingLevel());

		System.setProperty("tinylog.level", "error");
		readProperties();
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());

		System.setProperty("tinylog.level", "invalid");
		readProperties();
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());
	}

	/**
	 * Test reading logging format.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testFormat() throws Exception {
		LoggingWriter writer = new LoggingWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);

		System.setProperty("tinylog.format", "My log entry");
		readProperties();
		Logger.info("My message");
		assertEquals("My log entry" + NEW_LINE, writer.consumeEntry());

		System.setProperty("tinylog.format", "My log entry: {message}");
		readProperties();
		Logger.info("My message");
		assertEquals("My log entry: My message" + NEW_LINE, writer.consumeEntry());

		System.setProperty("tinylog.format", "My log entry: {message");
		readProperties();
		Logger.info("My message");
		assertEquals("My log entry: {message" + NEW_LINE, writer.consumeEntry());
	}

	/**
	 * Test reading stack trace limit.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testStackTrace() throws Exception {
		LoggingWriter writer = new LoggingWriter();
		Logger.setLoggingFormat("{message}");
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.ERROR);

		System.setProperty("tinylog.stacktrace", "1");
		readProperties();
		Logger.error(new Exception());
		String entry = writer.consumeEntry();
		assertNotNull(entry);
		assertEquals(3, entry.split(NEW_LINE).length);

		System.setProperty("tinylog.stacktrace", "5");
		readProperties();
		Logger.error(new Exception());
		entry = writer.consumeEntry();
		assertNotNull(entry);
		assertEquals(7, entry.split(NEW_LINE).length);

		System.setProperty("tinylog.stacktrace", "-1");
		readProperties();
		Logger.error(new Exception());
		entry = writer.consumeEntry();
		assertNotNull(entry);
		assertEquals(Thread.currentThread().getStackTrace().length, entry.split(NEW_LINE).length);

		Logger.setMaxStackTraceElements(1);
		System.setProperty("tinylog.stacktrace", "invalid");
		readProperties();
		Logger.error(new Exception());
		entry = writer.consumeEntry();
		assertNotNull(entry);
		assertEquals(3, entry.split(NEW_LINE).length);
	}

	/**
	 * Test reading logging writer.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testLoggingWriter() throws Exception {
		Logger.setLoggingFormat(null);
		Logger.setWriter(null);
		Logger.setLoggingLevel(ELoggingLevel.TRACE);

		ILoggingWriter writer = getWriter();
		assertNull(writer);

		System.setProperty("tinylog.writer", "console");
		readProperties();
		writer = getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleLoggingWriter.class, writer.getClass());

		System.setProperty("tinylog.writer", "null");
		readProperties();
		writer = getWriter();
		assertNull(writer);

		System.setProperty("tinylog.writer", "file");
		readProperties();
		writer = getWriter();
		assertNull(writer);

		File file = File.createTempFile("test", "tmp");
		System.setProperty("tinylog.writer.file", file.getAbsolutePath());
		readProperties();
		writer = getWriter();
		assertNotNull(writer);
		assertEquals(FileLoggingWriter.class, writer.getClass());
	}

	private void readProperties() throws Exception {
		Method method = Logger.class.getDeclaredMethod("readProperties");
		method.setAccessible(true);
		method.invoke(null);
	}

	private ILoggingWriter getWriter() throws Exception {
		Field field = Logger.class.getDeclaredField("loggingWriter");
		field.setAccessible(true);
		return (ILoggingWriter) field.get(null);
	}
}
