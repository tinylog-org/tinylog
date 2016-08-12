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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
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

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.h2.jdbc.JdbcDatabaseMetaData;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.mocks.SystemTimeMock;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.writers.JdbcWriter.Value;

import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for the SQL database writer.
 *
 * @see JdbcWriter
 */
public class JdbcWriterTest extends AbstractWriterTest {

	private static final String NEW_LINE = EnvironmentHelper.getNewLine();
	private static final String JDBC_URL = "jdbc:h2:mem:testDB";
	private static final String DATA_SOURCE_URL = "java:db/test";

	private Connection connection;
	private DriverManagerMock databaseMock;

	/**
	 * Create the database.
	 *
	 * @throws SQLException
	 *             Failed to create database
	 */
	@Before
	public final void init() throws SQLException {
		connection = DriverManager.getConnection(JDBC_URL);

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
		if (databaseMock != null) {
			databaseMock = null;
		}

		if (!connection.isClosed()) {
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
	}

	/**
	 * Test constructors without login data.
	 */
	@Test
	public final void testCreateInstanceWithoutLogin() {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE));
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), false);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), true);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), false, 1);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertEquals(1000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), true, 30);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertEquals(30000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), false);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), true);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), false, 1);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertEquals(1000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), true, 30);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertNull(writer.getUsername());
		assertNull(writer.getPassword());
		assertEquals(30000, writer.getReconnetInterval());
	}

	/**
	 * Test constructors with login data.
	 */
	@Test
	public final void testCreateInstanceWithLogin() {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), false, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), true, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), false, "admin", "123", 1);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(1000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE), true, "admin", "123", 30);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertNull(writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(30000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), false, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), true, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), false, "admin", "123", 1);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(1000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE), true, "admin", "123", 30);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(30000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, "admin", "123", 1);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(1000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, "admin", "123", 30);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(30000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, false, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, true, "admin", "123");
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertTrue(writer.getReconnetInterval() < 0);

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, false, "admin", "123", 1);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertFalse(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(1000, writer.getReconnetInterval());

		writer = new JdbcWriter(JDBC_URL, "log", new String[] { "LEVEL", "MESSAGE" }, new String[] { "level", "message" }, true, "admin", "123", 30);
		assertEquals(JDBC_URL, writer.getUrl());
		assertEquals("log", writer.getTable());
		assertEquals(Arrays.asList("LEVEL", "MESSAGE"), writer.getColumns());
		assertEquals(Arrays.asList(Value.LEVEL, Value.MESSAGE), writer.getValues());
		assertTrue(writer.isBatchMode());
		assertEquals("admin", writer.getUsername());
		assertEquals("123", writer.getPassword());
		assertEquals(30000, writer.getReconnetInterval());
	}

	/**
	 * Test required log entry values.
	 */
	@Test
	public final void testRequiredLogEntryValue() {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Collections.<Value> emptyList());
		assertEquals(Collections.<LogEntryValue> emptySet(), writer.getRequiredLogEntryValues());

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL, Value.MESSAGE));
		assertThat(writer.getRequiredLogEntryValues(), containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.values()));
		assertThat(writer.getRequiredLogEntryValues(), containsInAnyOrder(LogEntryValue.values()));
	}

	/**
	 * Test converting of string values.
	 */
	@Test
	public final void testStringValues() {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "date" }, null, null);
		assertEquals(Collections.singletonList(Value.DATE), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "DATE" }, null, null);
		assertEquals(Collections.singletonList(Value.DATE), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "pid" }, null, null);
		assertEquals(Collections.singletonList(Value.PROCESS_ID), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "PID" }, null, null);
		assertEquals(Collections.singletonList(Value.PROCESS_ID), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "thread" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_NAME), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "THREAD" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_NAME), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "thread_id" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_ID), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "THREAD_ID" }, null, null);
		assertEquals(Collections.singletonList(Value.THREAD_ID), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "context" }, null, null);
		assertEquals(Collections.singletonList(Value.CONTEXT), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "CONTEXT" }, null, null);
		assertEquals(Collections.singletonList(Value.CONTEXT), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "class" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "CLASS" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "class_name" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS_NAME), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "CLASS_NAME" }, null, null);
		assertEquals(Collections.singletonList(Value.CLASS_NAME), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "package" }, null, null);
		assertEquals(Collections.singletonList(Value.PACKAGE), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "PACKAGE" }, null, null);
		assertEquals(Collections.singletonList(Value.PACKAGE), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "method" }, null, null);
		assertEquals(Collections.singletonList(Value.METHOD), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "METHOD" }, null, null);
		assertEquals(Collections.singletonList(Value.METHOD), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "file" }, null, null);
		assertEquals(Collections.singletonList(Value.FILE), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "FILE" }, null, null);
		assertEquals(Collections.singletonList(Value.FILE), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "line" }, null, null);
		assertEquals(Collections.singletonList(Value.LINE), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "line" }, null, null);
		assertEquals(Collections.singletonList(Value.LINE), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "level" }, null, null);
		assertEquals(Collections.singletonList(Value.LEVEL), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "LEVEL" }, null, null);
		assertEquals(Collections.singletonList(Value.LEVEL), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "message" }, null, null);
		assertEquals(Collections.singletonList(Value.MESSAGE), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "MESSAGE" }, null, null);
		assertEquals(Collections.singletonList(Value.MESSAGE), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "exception" }, null, null);
		assertEquals(Collections.singletonList(Value.EXCEPTION), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "EXCEPTION" }, null, null);
		assertEquals(Collections.singletonList(Value.EXCEPTION), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "log_entry" }, null, null);
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), writer.getValues());
		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "log_entry" }, null, null);
		assertEquals(Collections.singletonList(Value.RENDERED_LOG_ENTRY), writer.getValues());

		writer = new JdbcWriter(JDBC_URL, "log", null, new String[] { "unknown" }, null, null);
		assertEquals("LOGGER WARNING: Unknown value type: \"unknown\"", getErrorStream().nextLine());
	}

	/**
	 * Test accepting and refusing of table names if database supports quoting.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testTableNamesWithQuotingSupport() throws SQLException, NamingException {
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
					assertEquals("Table name contains line breaks: log" + "\n" + "entries", ex.getMessage());
				}
				try {
					createAndCloseWriter("log" + "\r" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Table name contains line breaks: log" + "\r" + "entries", ex.getMessage());
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testTableNamesWithoutQuotingSupport() throws SQLException, NamingException {
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
					assertEquals("Illegal table name: log entries", ex.getMessage());
				}
				try {
					createAndCloseWriter("log\"'entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Illegal table name: log\"'entries", ex.getMessage());
				}
				try {
					createAndCloseWriter("log" + "\n" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Table name contains line breaks: log" + "\n" + "entries", ex.getMessage());
				}
				try {
					createAndCloseWriter("log" + "\r" + "entries");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Table name contains line breaks: log" + "\r" + "entries", ex.getMessage());
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testColumnNamesWithQuotingSupport() throws SQLException, NamingException {
		for (String identifierQuote : Arrays.asList("\"", "'", "`")) {
			DatabaseMetaDataMock mock = new DatabaseMetaDataMock(identifierQuote);
			try {
				createAndCloseWriter("log", "entry");
				createAndCloseWriter("log", "ENTRY");
				createAndCloseWriter("log", "entry2");
				createAndCloseWriter("log", "$entry$");
				createAndCloseWriter("log", "@entry@");
				createAndCloseWriter("log", "#entry#");
				createAndCloseWriter("log", "log_entry");
				createAndCloseWriter("log", "log entry");
				createAndCloseWriter("log", "log\"'entry");
				try {
					createAndCloseWriter("log", "log" + "\n" + "entry");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Column name contains line breaks: log" + "\n" + "entry", ex.getMessage());
				}
				try {
					createAndCloseWriter("log", "log" + "\r" + "entry");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Column name contains line breaks: log" + "\r" + "entry", ex.getMessage());
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testColumnNamesWithoutQuotingSupport() throws SQLException, NamingException {
		for (String identifierQuote : Arrays.asList(null, " ")) {
			DatabaseMetaDataMock mock = new DatabaseMetaDataMock(identifierQuote);
			try {
				createAndCloseWriter("log", "entry");
				createAndCloseWriter("log", "ENTRY");
				createAndCloseWriter("log", "entry2");
				createAndCloseWriter("log", "$entry$");
				createAndCloseWriter("log", "@entry@");
				createAndCloseWriter("log", "#entry#");
				createAndCloseWriter("log", "log_entry");
				try {
					createAndCloseWriter("log", "log entry");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Illegal column name: log entry", ex.getMessage());
				}
				try {
					createAndCloseWriter("log", "log\"'entry");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Illegal column name: log\"'entry", ex.getMessage());
				}
				try {
					createAndCloseWriter("log", "log" + "\n" + "entry");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Column name contains line breaks: log" + "\n" + "entry", ex.getMessage());
				}
				try {
					createAndCloseWriter("log", "log" + "\r" + "entry");
					fail("SQLException expected");
				} catch (SQLException ex) {
					assertEquals("Column name contains line breaks: log" + "\r" + "entry", ex.getMessage());
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testSizeOfColumnsAndValues() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("MESSAGE"), Arrays.asList(Value.MESSAGE));
		try {
			writer.init(null);
		} finally {
			writer.close();
		}

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		try {
			writer.init(null);
			fail("SQLException expected");
		} catch (SQLException ex) {
			assertEquals("Number of columns and values must be equal, but columns = 1 and values = 2", ex.getMessage());
		} finally {
			writer.close();
		}

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
		try {
			writer.init(null);
		} finally {
			writer.close();
		}

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("LEVEL", "MESSAGE"), Arrays.asList(Value.MESSAGE));
		try {
			writer.init(null);
			fail("SQLException expected");
		} catch (SQLException ex) {
			assertEquals("Number of columns and values must be equal, but columns = 2 and values = 1", ex.getMessage());
		} finally {
			writer.close();
		}
	}

	/**
	 * Test log in to database.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testLogin() throws SQLException, NamingException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE USER user PASSWORD '123'");
		statement.close();

		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Collections.<Value> emptyList(), "user", "123");
		writer.init(null);
		writer.close();

		statement = connection.createStatement();
		statement.execute("DROP USER user");
		statement.close();

		writer = new JdbcWriter(JDBC_URL, "log", Collections.<Value> emptyList(), "user", "123");
		try {
			writer.init(null);
			fail("SQLException expected");
		} catch (SQLException ex) {
			// Expected
		}
	}

	/**
	 * Test connecting to database via data source.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testDataSource() throws SQLException, NamingException {
		new DataSourceContextMock();

		JdbcWriter writer = new JdbcWriter(DATA_SOURCE_URL, "log", Collections.<Value> emptyList());
		writer.init(null);
		writer.close();

		Statement statement = connection.createStatement();
		statement.execute("CREATE USER user PASSWORD '123'");
		statement.close();

		writer = new JdbcWriter(DATA_SOURCE_URL, "log", Collections.<Value> emptyList(), "user", "123");
		writer.init(null);
		writer.close();
	}

	/**
	 * Test writing date.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testDate() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.DATE));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testProcessId() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.PROCESS_ID));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testThreadName() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.THREAD_NAME));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testThreadId() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.THREAD_ID));
		writer.init(null);

		writer.write(new LogEntryBuilder().thread(Thread.currentThread()).create());
		assertEquals(Arrays.asList(Long.toString(Thread.currentThread().getId())), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing mapped diagnostic context.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testLoggingContext() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.CONTEXT));
		writer.init(null);

		writer.write(new LogEntryBuilder().create());
		assertEquals(Arrays.asList((String) null), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().context("number", "42").create());
		assertEquals(Arrays.asList("number=42"), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().context("number", "42").context("pi", "3.14").create());
		assertThat(getLogEntries(), anyOf(contains("number=42, pi=3.14"), contains("pi=3.14, number=42")));

		writer.close();
	}

	/**
	 * Test writing full qualified class name.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testClass() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.CLASS));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testClassName() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.CLASS_NAME));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testPackage() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.PACKAGE));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testMethod() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.METHOD));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testFile() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.FILE));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testLineNumber() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LINE));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testLoggingLevel() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.LEVEL));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testMessage() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testException() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.EXCEPTION));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testRenderedLogEntry() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.RENDERED_LOG_ENTRY));
		writer.init(null);

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello World").create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		clearEntries();

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello World" + NEW_LINE).create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		writer.close();
	}

	/**
	 * Test writing multiple fields.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testMultipleFields() throws SQLException, NamingException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate("ALTER TABLE \"log\" ADD \"level\" VARCHAR");
		} finally {
			statement.close();
		}

		/* Without column defining */

		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE, Value.LEVEL));
		writer.init(null);
		writer.write(new LogEntryBuilder().message("Hello World").level(Level.ERROR).create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries("entry"));
		assertEquals(Arrays.asList("ERROR"), getLogEntries("level"));
		writer.close();

		clearEntries();

		/* With column defining */

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("entry", "level"), Arrays.asList(Value.MESSAGE, Value.LEVEL));
		writer.init(null);
		writer.write(new LogEntryBuilder().message("Hello World").level(Level.ERROR).create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries("entry"));
		assertEquals(Arrays.asList("ERROR"), getLogEntries("level"));
		writer.close();

		clearEntries();

		writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList("level", "entry"), Arrays.asList(Value.LEVEL, Value.MESSAGE));
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testBatchWriting() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE), true);
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
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testAutoFlushing() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE), true);
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
	 * Test detecting a broken database connection.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testBrokenConnection() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE), false, -1);
		writer.init(null);

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		shutdown();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertThat(getErrorStream().nextLine(), allOf(startsWith("LOGGER ERROR: Database connection is broken ("), endsWith(")")));

		while (getErrorStream().hasLines()) { // H2 writes errors into the console
			assertThat(getErrorStream().nextLine(), not(startsWith("LOGGER")));
		}

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertFalse(getErrorStream().hasLines());

		writer.close();
	}

	/**
	 * Test reestablishing a broken database connection if batch mode is disabled.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testReconnecting() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE), false, 0);
		writer.init(null);

		shutdown();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertThat(getErrorStream().nextLine(), allOf(startsWith("LOGGER ERROR: Database connection is broken ("), endsWith(")")));

		while (getErrorStream().hasLines()) { // H2 writes errors into the console
			assertThat(getErrorStream().nextLine(), not(startsWith("LOGGER")));
		}

		reestablish();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());
		assertThat(getErrorStream().nextLine(), is("LOGGER ERROR: Broken database connection has been reestablished"));

		writer.close();
	}

	/**
	 * Test reestablishing a broken database connection if batch mode is enabled.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testBatchReconnecting() throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE), true, 0);
		writer.init(null);

		shutdown();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertThat(getErrorStream().nextLine(), allOf(startsWith("LOGGER ERROR: Database connection is broken ("), endsWith(")")));

		while (getErrorStream().hasLines()) { // H2 writes errors into the console
			assertThat(getErrorStream().nextLine(), not(startsWith("LOGGER")));
		}

		reestablish();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertEquals(Collections.emptyList(), getLogEntries());
		assertThat(getErrorStream().nextLine(), is("LOGGER ERROR: Broken database connection has been reestablished"));

		writer.flush();
		assertEquals(Arrays.asList("Hello World"), getLogEntries());

		writer.close();
	}

	/**
	 * Test reestablishing a broken database connection after a defined time span.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testDelayedReconnecting() throws SQLException, NamingException {
		SystemTimeMock mock = new SystemTimeMock();
		try {
			JdbcWriter writer = new JdbcWriter(JDBC_URL, "log", Arrays.asList(Value.MESSAGE), false, 1);
			writer.init(null);

			shutdown();

			writer.write(new LogEntryBuilder().message("Hello World").create());
			assertThat(getErrorStream().nextLine(), allOf(startsWith("LOGGER ERROR: Database connection is broken ("), endsWith(")")));

			while (getErrorStream().hasLines()) { // H2 writes errors into the console
				assertThat(getErrorStream().nextLine(), not(startsWith("LOGGER")));
			}
			mock.setCurrentTimeMillis(1000);

			writer.write(new LogEntryBuilder().message("Hello World").create());
			assertFalse(getErrorStream().hasLines());

			reestablish();
			mock.setCurrentTimeMillis(1999);

			writer.write(new LogEntryBuilder().message("Hello World").create());
			assertFalse(getErrorStream().hasLines());
			assertEquals(Collections.emptyList(), getLogEntries());

			mock.setCurrentTimeMillis(2000);

			writer.write(new LogEntryBuilder().message("Hello World").create());
			assertEquals(Arrays.asList("Hello World"), getLogEntries());
			assertThat(getErrorStream().nextLine(), is("LOGGER ERROR: Broken database connection has been reestablished"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test reestablishing a broken data source connection.
	 *
	 * @throws SQLException
	 *             Test failed
	 * @throws NamingException
	 *             Test failed
	 */
	@Test
	public final void testReconnectingViaDataSource() throws SQLException, NamingException {
		DataSourceContextMock mock = new DataSourceContextMock();

		JdbcWriter writer = new JdbcWriter(DATA_SOURCE_URL, "log", Arrays.asList(Value.MESSAGE), false, 0);
		writer.init(null);

		shutdown();
		mock.tearDown();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertThat(getErrorStream().nextLine(), allOf(startsWith("LOGGER ERROR: Database connection is broken ("), endsWith(")")));

		writer.write(new LogEntryBuilder().message("Hello World").create());
		// The same error won't be printed again

		while (getErrorStream().hasLines()) { // H2 writes errors into the console
			assertThat(getErrorStream().nextLine(), not(startsWith("LOGGER")));
		}

		reestablish();
		mock = new DataSourceContextMock();

		writer.write(new LogEntryBuilder().message("Hello World").create());
		assertEquals(Arrays.asList("Hello World"), getLogEntries());
		assertThat(getErrorStream().nextLine(), is("LOGGER ERROR: Broken database connection has been reestablished"));

		writer.close();
		mock.tearDown();
	}

	/**
	 * Test reading JDBC writer from properties.
	 */
	@Test
	public final void testFromProperties() {
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "jdbc");
		List<Writer> writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertEquals("LOGGER ERROR: Missing required property \"tinylog.writer.url\"", getErrorStream().nextLine());
		assertEquals("LOGGER ERROR: Failed to initialize jdbc writer", getErrorStream().nextLine());

		propertiesBuilder.set("tinylog.writer.url", "jdbc:");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertEquals("LOGGER ERROR: Missing required property \"tinylog.writer.table\"", getErrorStream().nextLine());
		assertEquals("LOGGER ERROR: Failed to initialize jdbc writer", getErrorStream().nextLine());

		propertiesBuilder.set("tinylog.writer.url", "jdbc:").set("tinylog.writer.table", "log");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertEquals("LOGGER ERROR: Missing required property \"tinylog.writer.values\"", getErrorStream().nextLine());
		assertEquals("LOGGER ERROR: Failed to initialize jdbc writer", getErrorStream().nextLine());

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
		assertTrue(jdbcWriter.getReconnetInterval() < 0);

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
		assertTrue(jdbcWriter.getReconnetInterval() < 0);

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
		assertTrue(jdbcWriter.getReconnetInterval() < 0);

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
		assertTrue(jdbcWriter.getReconnetInterval() < 0);

		propertiesBuilder.remove("tinylog.writer.batch").set("tinylog.writer.reconnect", "1");
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
		assertEquals(1000, jdbcWriter.getReconnetInterval());

		propertiesBuilder.remove("tinylog.writer.batch").set("tinylog.writer.reconnect", "-1");
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
		assertTrue(jdbcWriter.getReconnetInterval() < 0);

		propertiesBuilder.remove("tinylog.writer.batch").set("tinylog.writer.reconnect", "30");
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
		assertEquals(30000, jdbcWriter.getReconnetInterval());

		propertiesBuilder.remove("tinylog.writer.reconnect").set("tinylog.writer.username", "admin").set("tinylog.writer.password", "123");
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
		assertTrue(jdbcWriter.getReconnetInterval() < 0);
	}

	private void shutdown() throws SQLException {
		if (databaseMock == null) {
			databaseMock = new DriverManagerMock();
		}

		Statement statement = connection.createStatement();
		try {
			statement.execute("SHUTDOWN");
		} finally {
			statement.close();
		}
	}

	private void reestablish() throws SQLException {
		if (databaseMock != null) {
			databaseMock.tearDown();
			databaseMock = null;
		}

		init();
	}

	private void createAndCloseWriter(final String table, final String... columns) throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, table, Collections.<Value> emptyList());
		try {
			writer.init(null);
		} finally {
			writer.close();
		}
	}

	private void createAndCloseWriter(final String table, final String column) throws SQLException, NamingException {
		JdbcWriter writer = new JdbcWriter(JDBC_URL, table, Collections.singletonList(column), Collections.singletonList(Value.RENDERED_LOG_ENTRY));
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

	private static final class DriverManagerMock extends MockUp<DriverManager> {

		@Mock
		public Connection getConnection(final String url) throws SQLException {
			throw new SQLException("No connection");
		}

		@Mock
		public Connection getConnection(final String url, final String user, final String password) throws SQLException {
			throw new SQLException("No connection");
		}

	}

	private static final class DataSourceContextMock extends MockUp<InitialContext> {

		@Mock
		public Object lookup(final String name) throws NamingException {
			if (DATA_SOURCE_URL.equals(name)) {
				JdbcDataSource dataSource = new JdbcDataSource();
				dataSource.setURL(JDBC_URL);
				return dataSource;
			} else {
				throw new NameNotFoundException();
			}
		}

	}

}
