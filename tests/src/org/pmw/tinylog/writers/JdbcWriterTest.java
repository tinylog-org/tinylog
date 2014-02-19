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

package org.pmw.tinylog.writers;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;

import org.h2.jdbc.JdbcDatabaseMetaData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.util.StringListOutputStream;
import org.pmw.tinylog.writers.JdbcWriter.Value;

/**
 * Tests for the SQL database writer.
 * 
 * @see JdbcWriter
 */
public class JdbcWriterTest extends AbstractWriterTest {

	private static final String NEW_LINE = EnvironmentHelper.getNewLine();
	private static final String URL = "jdbc:h2:mem:testDB";

	private Connection connection;

	/**
	 * Create the database.
	 * 
	 * @throws SQLException
	 *             Failed to create database
	 */
	@Before
	public final void init() throws SQLException {
		connection = DriverManager.getConnection(URL);

		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate("CREATE TABLE \"log\" (\"entry\" VARCHAR)");
		} finally {
			statement.close();
		}
	}

	/**
	 * Remove the database.
	 * 
	 * @throws SQLException
	 *             Failed to remove database
	 */
	@After
	public final void dispose() throws SQLException {
		try {
			Statement statement = connection.createStatement();
			try {
				statement.execute("SHUTDOWN");
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	/**
	 * Test all constructors.
	 */
	@Test
	public final void testCreateInstance() {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE));
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), false);
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), true);
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), false, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), true, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), false);
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), true);
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), false, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), true, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, false, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());

		writer = new JdbcWriter(URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, true, "admin", "123");
		assertEquals(URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
	}

	/**
	 * Test required log entry values.
	 */
	@Test
	public final void testRequiredLogEntryValue() {
		JdbcWriter writer = new JdbcWriter(URL, "log", Collections.<Value> emptyList());
		assertEquals(Collections.<LogEntryValue> emptySet(), writer.getRequiredLogEntryValues());

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE));
		assertThat(writer.getRequiredLogEntryValues(), containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));

		writer = new JdbcWriter(URL, "log", Arrays.asList(Value.values()));
		assertThat(writer.getRequiredLogEntryValues(), containsInAnyOrder(LogEntryValue.values()));
	}

	/**
	 * Test converting of string values.
	 */
	@Test
	public final void testStringValues() {
		JdbcWriter writer = new JdbcWriter(URL, "log", null, new String[] { "date" }, null, null);
		assertEquals(Collections.singletonList(Value.DATE), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "DATE" }, null, null);
		assertEquals(Collections.singletonList(Value.DATE), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "pid" }, null, null);
		assertEquals(Collections.singletonList(Value.PROCESS_ID), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "PID" }, null, null);
		assertEquals(Collections.singletonList(Value.PROCESS_ID), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "thread" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_NAME), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "THREAD" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_NAME), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "thread_id" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_ID), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "THREAD_ID" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_ID), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "class" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "CLASS" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "class_name" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS_NAME), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "CLASS_NAME" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS_NAME), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "package" }, null, null);
		assertEquals(Collections.singletonList(Value.PACKAGE), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "PACKAGE" }, null, null);
		assertEquals(Collections.singletonList(Value.PACKAGE), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "method" }, null, null);
		assertEquals(Collections.singletonList(Value.METHOD), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "METHOD" }, null, null);
		assertEquals(Collections.singletonList(Value.METHOD), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "file" }, null, null);
		assertEquals(Collections.singletonList(Value.FILE), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "FILE" }, null, null);
		assertEquals(Collections.singletonList(Value.FILE), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "line" }, null, null);
		assertEquals(Collections.singletonList(Value.LINE), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "line" }, null, null);
		assertEquals(Collections.singletonList(Value.LINE), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "level" }, null, null);
		assertEquals(Collections.singletonList(Value.LEVEL), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "LEVEL" }, null, null);
		assertEquals(Collections.singletonList(Value.LEVEL), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "message" }, null, null);
		assertEquals(Collections.singletonList(Value.MESSAGE), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "MESSAGE" }, null, null);
		assertEquals(Collections.singletonList(Value.MESSAGE), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "exception" }, null, null);
		assertEquals(Collections.singletonList(Value.EXCEPTION), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "EXCEPTION" }, null, null);
		assertEquals(Collections.singletonList(Value.EXCEPTION), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "log_entry" }, null, null);
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), writer.getValues());
		writer = new JdbcWriter(URL, "log", null, new String[] { "log_entry" }, null, null);
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), writer.getValues());

		writer = new JdbcWriter(URL, "log", null, new String[] { "unknown" }, null, null);
		assertThat(getErrorStream().nextLine(), containsString("unknown"));
	}

	/**
	 * Test accepting and refusing of table names if database supports quoting.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testTableNamesWithQuotingSupport() throws SQLException {
		for (String identifierQuote : Arrays.asList("\"", "'", "`")) {
			DatabaseMetaDataMock mock = new DatabaseMetaDataMock(identifierQuote);
			try {
				createAndCloseWriter("log");
				createAndCloseWriter("LOG");
				createAndCloseWriter("log2");
				createAndCloseWriter("$log$");
				createAndCloseWriter("@log@");
				createAndCloseWriter("#log#");
				createAndCloseWriter("log_entries");
				createAndCloseWriter("log entries");
				createAndCloseWriter("log\"'entries");
				try {
					createAndCloseWriter("log" + "\n" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\n" + "entries"));
				}
				try {
					createAndCloseWriter("log" + "\r" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\r" + "entries"));
				}
			} finally {
				mock.tearDown();
			}
		}
	}

	/**
	 * Test accepting and refusing of table names if database doesn't supports quoting.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testTableNamesWithoutQuotingSupport() throws SQLException {
		for (String identifierQuote : Arrays.asList(null, " ")) {
			DatabaseMetaDataMock mock = new DatabaseMetaDataMock(identifierQuote);
			try {
				createAndCloseWriter("log");
				createAndCloseWriter("LOG");
				createAndCloseWriter("log2");
				createAndCloseWriter("$log$");
				createAndCloseWriter("@log@");
				createAndCloseWriter("#log#");
				createAndCloseWriter("log_entries");
				try {
					createAndCloseWriter("log entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log entries"));
				}
				try {
					createAndCloseWriter("log\"'entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log\"'entries"));
				}
				try {
					createAndCloseWriter("log" + "\n" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\n" + "entries"));
				}
				try {
					createAndCloseWriter("log" + "\r" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\r" + "entries"));
				}
			} finally {
				mock.tearDown();
			}
		}
	}

	/**
	 * Test accepting and refusing of column names if database supports quoting.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testColumnNamesWithQuotingSupport() throws SQLException {
		for (String identifierQuote : Arrays.asList("\"", "'", "`")) {
			DatabaseMetaDataMock mock = new DatabaseMetaDataMock(identifierQuote);
			try {
				createAndCloseWriter("log", "entry");
				createAndCloseWriter("log", "ENTRY");
				createAndCloseWriter("log", "entry2");
				createAndCloseWriter("log", "$entry$");
				createAndCloseWriter("log", "@entry@");
				createAndCloseWriter("log", "#entry#");
				createAndCloseWriter("log", "log_entries");
				createAndCloseWriter("log", "log entries");
				createAndCloseWriter("log", "log\"'entries");
				try {
					createAndCloseWriter("log", "log" + "\n" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\n" + "entries"));
				}
				try {
					createAndCloseWriter("log", "log" + "\r" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\r" + "entries"));
				}
			} finally {
				mock.tearDown();
			}
		}
	}

	/**
	 * Test accepting and refusing of column names if database doesn't supports quoting.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testColumnNamesWithoutQuotingSupport() throws SQLException {
		for (String identifierQuote : Arrays.asList(null, " ")) {
			DatabaseMetaDataMock mock = new DatabaseMetaDataMock(identifierQuote);
			try {
				createAndCloseWriter("log", "entry");
				createAndCloseWriter("log", "ENTRY");
				createAndCloseWriter("log", "entry2");
				createAndCloseWriter("log", "$entry$");
				createAndCloseWriter("log", "@entry@");
				createAndCloseWriter("log", "#entry#");
				createAndCloseWriter("log", "log_entries");
				try {
					createAndCloseWriter("log", "log entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log entries"));
				}
				try {
					createAndCloseWriter("log", "log\"'entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log\"'entries"));
				}
				try {
					createAndCloseWriter("log", "log" + "\n" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\n" + "entries"));
				}
				try {
					createAndCloseWriter("log", "log" + "\r" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertThat(ex.getMessage(), containsString("log" + "\r" + "entries"));
				}
			} finally {
				mock.tearDown();
			}
		}
	}

	/**
	 * Test error handling if the lists columns and values have different sizes.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testSizeOfColumnsAndValues() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList("MESSAGE"), Arrays.asList(Value.MESSAGE));
		try {
			writer.init(null);
		} finally {
			writer.close();
		}

		writer = new JdbcWriter(URL, "log", Arrays.asList("MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		try {
			writer.init(null);
			fail("SQLException expected");
		} catch (SQLException ex) {
			assertThat(ex.getMessage(), allOf(containsString("1"), containsString("2")));
		} finally {
			writer.close();
		}

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		try {
			writer.init(null);
		} finally {
			writer.close();
		}

		writer = new JdbcWriter(URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.MESSAGE));
		try {
			writer.init(null);
			fail("SQLException expected");
		} catch (SQLException ex) {
			assertThat(ex.getMessage(), allOf(containsString("2"), containsString("1")));
		} finally {
			writer.close();
		}
	}

	/**
	 * Test log in to database.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testLogin() throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE USER user PASSWORD '123'");
		statement.close();

		JdbcWriter writer = new JdbcWriter(URL, "log", Collections.<Value> emptyList(), "user", "123");
		writer.init(null);
		writer.close();

		statement = connection.createStatement();
		statement.execute("DROP USER user");
		statement.close();

		writer = new JdbcWriter(URL, "log", Collections.<Value> emptyList(), "user", "123");
		try {
			writer.init(null);
			fail("SQLException expected");
		} catch (SQLException ex) {
			// Expected
		}
	}

	/**
	 * Test writing date.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testDate() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.DATE));
		writer.init(null);

		writer.write(new LogEntryBuilder().date(new Date(1000)).create());
		assertEquals(Arrays.asList(new Timestamp(1000).toString()), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing process ID (pid).
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testProcessId() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.PROCESS_ID));
		writer.init(null);

		writer.write(new LogEntryBuilder().processId("42").create());
		assertEquals(Arrays.asList("42"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing thread name.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testThreadName() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.THREAD_NAME));
		writer.init(null);

		writer.write(new LogEntryBuilder().thread(Thread.currentThread()).create());
		assertEquals(Arrays.asList(Thread.currentThread().getName()), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing thread ID.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testThreadId() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.THREAD_ID));
		writer.init(null);

		writer.write(new LogEntryBuilder().thread(Thread.currentThread()).create());
		assertEquals(Arrays.asList(Long.toString(Thread.currentThread().getId())), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing full qualified class name.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testClass() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.CLASS));
		writer.init(null);

		writer.write(new LogEntryBuilder().className(JdbcWriterTest.class.getName()).create());
		assertEquals(Arrays.asList(JdbcWriterTest.class.getName()), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing class name without package.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testClassName() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.CLASS_NAME));
		writer.init(null);

		writer.write(new LogEntryBuilder().className(JdbcWriterTest.class.getName()).create());
		assertEquals(Arrays.asList(JdbcWriterTest.class.getSimpleName()), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().className(JdbcWriterTest.class.getSimpleName()).create());
		assertEquals(Arrays.asList(JdbcWriterTest.class.getSimpleName()), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing package name.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testPackage() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.PACKAGE));
		writer.init(null);

		writer.write(new LogEntryBuilder().className(JdbcWriterTest.class.getName()).create());
		assertEquals(Arrays.asList(JdbcWriterTest.class.getPackage().getName()), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().className(JdbcWriterTest.class.getSimpleName()).create());
		assertEquals(Arrays.asList(""), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing method name.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testMethod() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.METHOD));
		writer.init(null);

		writer.write(new LogEntryBuilder().method(null).create());
		assertEquals(Arrays.asList((String) null), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().method("doIt").create());
		assertEquals(Arrays.asList("doIt"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing source filename.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testFile() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.FILE));
		writer.init(null);

		writer.write(new LogEntryBuilder().file(null).create());
		assertEquals(Arrays.asList((String) null), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().file("MyFile.java").create());
		assertEquals(Arrays.asList("MyFile.java"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing line number.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testLineNumber() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LINE));
		writer.init(null);

		writer.write(new LogEntryBuilder().lineNumber(-1).create());
		assertEquals(Arrays.asList((String) null), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().lineNumber(42).create());
		assertEquals(Arrays.asList("42"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing message.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testLoggingLevel() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.LEVEL));
		writer.init(null);

		writer.write(new LogEntryBuilder().level(Level.ERROR).create());
		assertEquals(Arrays.asList("ERROR"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing exception.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testMessage() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.MESSAGE));
		writer.init(null);

		writer.write(new LogEntryBuilder().message(null).create());
		assertEquals(Arrays.asList((String) null), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing rendered log entry.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testException() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.EXCEPTION));
		writer.init(null);

		writer.write(new LogEntryBuilder().exception(null).create());
		assertEquals(Arrays.asList((String) null), getLogEntries());

		clearEntries();

		Exception exception = new Exception();
		exception.setStackTrace(new StackTraceElement[0]);
		writer.write(new LogEntryBuilder().exception(exception).create());
		assertEquals(Arrays.asList(Exception.class.getName()), getLogEntries());

		clearEntries();

		exception = new Exception("Text");
		exception.setStackTrace(new StackTraceElement[0]);
		writer.write(new LogEntryBuilder().exception(exception).create());
		assertEquals(Arrays.asList(exception.getClass().getName() + ": Text"), getLogEntries());

		clearEntries();

		exception = new Exception();
		StackTraceElement stackTraceElement = new StackTraceElement("a.b.MyClass", "doIt", null, -1);
		exception.setStackTrace(new StackTraceElement[] { stackTraceElement });
		writer.write(new LogEntryBuilder().exception(exception).create());
		assertEquals(Arrays.asList(exception.getClass().getName() + NEW_LINE + "\t" + "at " + stackTraceElement), getLogEntries());

		clearEntries();

		Exception subException = new NullPointerException();
		subException.setStackTrace(new StackTraceElement[0]);
		exception = new Exception(null, subException);
		exception.setStackTrace(new StackTraceElement[0]);
		writer.write(new LogEntryBuilder().exception(exception).create());
		assertEquals(Arrays.asList(exception.getClass().getName() + NEW_LINE + "Caused by: " + subException.getClass().getName()), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing .
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testRenderedLogEntry() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.RENDERED_LOG_ENTRY));
		writer.init(null);

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello World").create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing multiple fields.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testMultipleFields() throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate("ALTER TABLE \"log\" ADD \"level\" VARCHAR");
		} finally {
			statement.close();
		}

		/* Without column defining */

		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.MESSAGE, Value.LEVEL));
		writer.init(null);
		writer.write(new LogEntryBuilder().message("Hello World").level(Level.ERROR).create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries("entry"));
		assertEquals(Arrays.asList("ERROR"), getLogEntries("level"));
		writer.close();

		clearEntries();

		/* With column defining */

		writer = new JdbcWriter(URL, "log", Arrays.asList("entry", "level"), Arrays.asList(Value.MESSAGE, Value.LEVEL));
		writer.init(null);
		writer.write(new LogEntryBuilder().message("Hello World").level(Level.ERROR).create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries("entry"));
		assertEquals(Arrays.asList("ERROR"), getLogEntries("level"));
		writer.close();

		clearEntries();

		writer = new JdbcWriter(URL, "log", Arrays.asList("level", "entry"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		writer.init(null);
		writer.write(new LogEntryBuilder().message("Hello World").level(Level.ERROR).create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries("entry"));
		assertEquals(Arrays.asList("ERROR"), getLogEntries("level"));
		writer.close();
	}

	/**
	 * Test batch writing.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testBatchWriting() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.MESSAGE), true);
		writer.init(null);

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertEquals(Collections.emptyList(), getLogEntries());

		writer.flush();
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		writer.close();
	}

	/**
	 * Test auto flushing after many entries for batch writing.
	 * 
	 * @throws SQLException
	 *             Test failed
	 */
	@Test
	public final void testAutoFlushing() throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, "log", Arrays.asList(Value.MESSAGE), true);
		writer.init(null);

		for (int i = 0; i < 1000; ++i) {
			writer.write(new LogEntryBuilder().message("Entry: " + (i + 1)).create());
		}
		assertNotEquals(0, getLogEntries().size());
		assertEquals("Entry: 1", getLogEntries().get(0));

		writer.flush();
		assertEquals(1000, getLogEntries().size());
		assertEquals("Entry: 1", getLogEntries().get(0));
		assertEquals("Entry: 1000", getLogEntries().get(999));

		writer.close();
	}

	/**
	 * Test reading JDBC writer from properties.
	 */
	@Test
	public final void testFromProperties() {
		StringListOutputStream errorStream = getErrorStream();

		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "jdbc");
		List<Writer> writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.url")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("jdbc writer")));
		assertFalse(errorStream.hasLines());

		propertiesBuilder.set("tinylog.writer.url", "jdbc:");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.table")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("jdbc writer")));
		assertFalse(errorStream.hasLines());

		propertiesBuilder.set("tinylog.writer.url", "jdbc:").set("tinylog.writer.table", "log");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.values")));
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("jdbc writer")));
		assertFalse(errorStream.hasLines());

		propertiesBuilder.set("tinylog.writer.values", "log_entry");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(JdbcWriter.class));
		JdbcWriter jdbcWriter = (JdbcWriter) writers.get(0);
		assertEquals("jdbc:", jdbcWriter.getUrl());
		assertEquals("log", jdbcWriter.getTable());
		assertNull(jdbcWriter.getColumns());
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), jdbcWriter.getValues());
		assertFalse(jdbcWriter.isBatchMode());
		assertNull(jdbcWriter.getUsername());
		assertNull(jdbcWriter.getPassword());

		propertiesBuilder.set("tinylog.writer.columns", "ENTRY");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(JdbcWriter.class));
		jdbcWriter = (JdbcWriter) writers.get(0);
		assertEquals("jdbc:", jdbcWriter.getUrl());
		assertEquals("log", jdbcWriter.getTable());
		assertEquals(Collections.singletonList("ENTRY"), jdbcWriter.getColumns());
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), jdbcWriter.getValues());
		assertFalse(jdbcWriter.isBatchMode());
		assertNull(jdbcWriter.getUsername());
		assertNull(jdbcWriter.getPassword());

		propertiesBuilder.remove("tinylog.writer.columns").set("batch", "false");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(JdbcWriter.class));
		jdbcWriter = (JdbcWriter) writers.get(0);
		assertEquals("jdbc:", jdbcWriter.getUrl());
		assertEquals("log", jdbcWriter.getTable());
		assertNull(jdbcWriter.getColumns());
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), jdbcWriter.getValues());
		assertFalse(jdbcWriter.isBatchMode());
		assertNull(jdbcWriter.getUsername());
		assertNull(jdbcWriter.getPassword());

		propertiesBuilder.set("tinylog.writer.batch", "true");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(JdbcWriter.class));
		jdbcWriter = (JdbcWriter) writers.get(0);
		assertEquals("jdbc:", jdbcWriter.getUrl());
		assertEquals("log", jdbcWriter.getTable());
		assertNull(jdbcWriter.getColumns());
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), jdbcWriter.getValues());
		assertTrue(jdbcWriter.isBatchMode());
		assertNull(jdbcWriter.getUsername());
		assertNull(jdbcWriter.getPassword());

		propertiesBuilder.remove("tinylog.writer.batch").set("tinylog.writer.username", "admin").set("tinylog.writer.password", "123");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(JdbcWriter.class));
		jdbcWriter = (JdbcWriter) writers.get(0);
		assertEquals("jdbc:", jdbcWriter.getUrl());
		assertEquals("log", jdbcWriter.getTable());
		assertNull(jdbcWriter.getColumns());
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), jdbcWriter.getValues());
		assertFalse(jdbcWriter.isBatchMode());
		assertEquals("admin", jdbcWriter.getUsername());
		assertEquals("123", jdbcWriter.getPassword());
	}

	private void createAndCloseWriter(final String table, final String... columns) throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, table, Collections.<Value> emptyList());
		try {
			writer.init(null);
		} finally {
			writer.close();
		}
	}

	private void createAndCloseWriter(final String table, final String column) throws SQLException {
		JdbcWriter writer = new JdbcWriter(URL, table, Collections.singletonList(column), Collections.singletonList(Value.RENDERED_LOG_ENTRY));
		try {
			writer.init(null);
		} finally {
			writer.close();
		}
	}

	private List<String> getLogEntries() throws SQLException {
		return getLogEntries("entry");
	}

	private List<String> getLogEntries(final String field) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet resultSet = statement.executeQuery("SELECT \"" + field + "\" FROM \"log\"");
			try {
				List<String> entries = new ArrayList<>();
				while (resultSet.next()) {
					entries.add(resultSet.getString(field));
				}
				return entries;
			} finally {
				resultSet.close();
			}
		} finally {
			statement.close();
		}
	}

	private void clearEntries() throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate("TRUNCATE TABLE \"log\"");
		} finally {
			statement.close();
		}
	}

	private static final class DatabaseMetaDataMock extends MockUp<JdbcDatabaseMetaData> {

		private final String identifierQuote;

		private DatabaseMetaDataMock(final String identifierQuote) {
			this.identifierQuote = identifierQuote;
		}

		@Mock
		public String getIdentifierQuoteString() {
			return identifierQuote;
		}

	}

}
