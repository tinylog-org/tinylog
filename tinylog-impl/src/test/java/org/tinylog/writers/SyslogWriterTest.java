/*
 * Copyright 2023 Piotr Karlowicz
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

package org.tinylog.writers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.util.LogEntryBuilder;
import org.tinylog.util.TcpSyslogServer;
import org.tinylog.util.UdpSyslogServer;
import org.tinylog.writers.raw.SyslogFacility;
import org.tinylog.writers.raw.SyslogSeverity;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.tinylog.util.Maps.doubletonMap;
import static org.tinylog.util.Maps.tripletonMap;

/**
 * Tests for {@link SyslogWriter}.
 */
public final class SyslogWriterTest {

	private static final Integer TEST_PORT_NUMBER = 9999;
	private static final String TEST_MESSAGE = "Test Message";
	
	private String getExpectedSyslogMessage(final String message,
						final SyslogFacility facility,
						final SyslogSeverity severity,
						final String identification) {
		int code = (facility.getCode() << 3) + severity.getCode();
		return "<" + code + ">" + identification + ": " + message;
	}

	/**
	 * Sends udp message with default settings and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendDefaultUdpSyslogMessage() throws Exception {
		UdpSyslogServer server = new UdpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogWriter writer = new SyslogWriter(tripletonMap("format", "{message}", "protocol", "udp", "port", TEST_PORT_NUMBER.toString()));
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, SyslogFacility.USER, SyslogSeverity.INFO, ""));
		server.shutdown();
	}
	
	/**
	 * Sends udp message with non-default settings and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendNondefaultUdpSyslogMessage() throws Exception {
		UdpSyslogServer server = new UdpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogFacility facility = SyslogFacility.LOCAL0;
		SyslogSeverity severity = SyslogSeverity.ERROR;
		Map<String, String> properties = new HashMap<>();
		properties.put("format", "{message}");
		properties.put("protocol", "udp");
		properties.put("port", TEST_PORT_NUMBER.toString());
		properties.put("facility", facility.toString());
		properties.put("severity", severity.toString());

		SyslogWriter writer = new SyslogWriter(properties);
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, severity, ""));
		server.shutdown();
	}

	/**
	 * Verifies that default UDP prrotocol will be used if no protocol is specified.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void missingProtocol() throws Exception {
		UdpSyslogServer server = new UdpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogWriter writer = new SyslogWriter(doubletonMap("format", "{message}", "port", TEST_PORT_NUMBER.toString()));
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, SyslogFacility.USER, SyslogSeverity.INFO, ""));
		server.shutdown();
	}
	
	/**
	 * Verifies that an exception will be thrown, if protocol name  is invalid.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void invalidProtocol() throws Exception {
		assertThatThrownBy(() -> new SyslogWriter(singletonMap("protocol", "invalid"))).hasMessageMatching("(?i).*protocol.*");
	}
	
	/**
	 * Verifies that an exception will be thrown, if facility is invalid.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void invalidFacility() throws Exception {
		assertThatThrownBy(() -> new SyslogWriter(doubletonMap("protocol", "udp", "facility", "invalid"))
				.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create())).hasMessageMatching("(?i).*SyslogFacility.*");
	}
	
	/**
	 * Verifies that an exception will be thrown, if severity is invalid.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void invalidSeverity() throws Exception {
		assertThatThrownBy(() -> new SyslogWriter(doubletonMap("protocol", "udp", "severity", "invalid"))
				.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create())).hasMessageMatching("(?i).*SyslogSeverity.*");
	}
	
	/**
	 * Sends udp message with specified  identification string and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendUdpSyslogMessageWithIdentification() throws Exception {
		UdpSyslogServer server = new UdpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogFacility facility = SyslogFacility.KERN;
		SyslogSeverity severity = SyslogSeverity.DEBUG;
		String identification = "SyslogWriterTest";
		Map<String, String> properties = new HashMap<>();
		properties.put("format", "{message}");
		properties.put("protocol", "udp");
		properties.put("port", TEST_PORT_NUMBER.toString());
		properties.put("facility", facility.toString());
		properties.put("severity", severity.toString());
		properties.put("identification", identification);

		SyslogWriter writer = new SyslogWriter(properties);
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, severity, identification));
		server.shutdown();
	}
	
	/**
	 * Sends udp message with different levels and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendLevelControlledUdpSyslogMessage() throws Exception {
		UdpSyslogServer server = new UdpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogFacility facility = SyslogFacility.AUTH;
		Map<String, String> properties = new HashMap<>();
		properties.put("format", "{message}");
		properties.put("protocol", "udp");
		properties.put("port", TEST_PORT_NUMBER.toString());
		properties.put("facility", facility.toString());

		SyslogWriter writer = new SyslogWriter(properties);
		LogEntryBuilder log = LogEntryBuilder.empty().message(TEST_MESSAGE);
		log.level(Level.DEBUG);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.DEBUG, ""));

		log.level(Level.ERROR);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.ERROR, ""));

		log.level(Level.INFO);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.INFO, ""));

		log.level(Level.OFF);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.EMERG, ""));

		log.level(Level.TRACE);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.DEBUG, ""));

		log.level(Level.WARN);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.WARNING, ""));
		server.shutdown();
	}
	
	/**
	 * Sends tcp message with default settings and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendDefaultTcpSyslogMessage() throws Exception {
		TcpSyslogServer server = new TcpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogWriter writer = new SyslogWriter(tripletonMap("format", "{message}", "protocol", "tcp", "port", TEST_PORT_NUMBER.toString()));
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, SyslogFacility.USER, SyslogSeverity.INFO, ""));
		server.shutdown();
	}
	
	/**
	 * Sends tcp message with non-default settings and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendNondefaultTcpSyslogMessage() throws Exception {
		TcpSyslogServer server = new TcpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogFacility facility = SyslogFacility.LOCAL0;
		SyslogSeverity severity = SyslogSeverity.ERROR;
		Map<String, String> properties = new HashMap<>();
		properties.put("format", "{message}");
		properties.put("protocol", "tcp");
		properties.put("port", TEST_PORT_NUMBER.toString());
		properties.put("facility", facility.toString());
		properties.put("severity", severity.toString());

		SyslogWriter writer = new SyslogWriter(properties);
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, severity, ""));
		server.shutdown();
	}
	
	/**
	 * Sends tcp message with specified  identification string and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendTcpSyslogMessageWithIdentification() throws Exception {
		TcpSyslogServer server = new TcpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogFacility facility = SyslogFacility.KERN;
		SyslogSeverity severity = SyslogSeverity.DEBUG;
		String identification = "SyslogWriterTest";
		Map<String, String> properties = new HashMap<>();
		properties.put("format", "{message}");
		properties.put("protocol", "tcp");
		properties.put("port", TEST_PORT_NUMBER.toString());
		properties.put("facility", facility.toString());
		properties.put("severity", severity.toString());
		properties.put("identification", identification);

		SyslogWriter writer = new SyslogWriter(properties);
		writer.write(LogEntryBuilder.empty().message(TEST_MESSAGE).create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, severity, identification));
		server.shutdown();
	}

	/**
	 * Sends tcp message with different levels and verifies it is received.
	 * 
	 * @throws Exception Failed.
	 */
	@Test
	public void sendLevelControlledTcpSyslogMessage() throws Exception {
		TcpSyslogServer server = new TcpSyslogServer(TEST_PORT_NUMBER);
		server.start();

		SyslogFacility facility = SyslogFacility.AUTH;
		Map<String, String> properties = new HashMap<>();
		properties.put("format", "{message}");
		properties.put("protocol", "tcp");
		properties.put("port", TEST_PORT_NUMBER.toString());
		properties.put("facility", facility.toString());

		SyslogWriter writer = new SyslogWriter(properties);
		LogEntryBuilder log = LogEntryBuilder.empty().message(TEST_MESSAGE);
		log.level(Level.DEBUG);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.DEBUG, ""));

		log.level(Level.ERROR);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.ERROR, ""));

		log.level(Level.INFO);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.INFO, ""));

		log.level(Level.OFF);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.EMERG, ""));

		log.level(Level.TRACE);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.DEBUG, ""));

		log.level(Level.WARN);
		writer.write(log.create());

		Thread.sleep(250);
		assertThat(server.getLastMessage()).isEqualTo(getExpectedSyslogMessage(TEST_MESSAGE, facility, SyslogSeverity.WARNING, ""));
		server.shutdown();
	}

}
