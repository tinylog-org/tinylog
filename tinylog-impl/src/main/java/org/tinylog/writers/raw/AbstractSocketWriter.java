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

package org.tinylog.writers.raw;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.writers.AbstractFormatPatternWriter;

/**
 * Base writer for outputting log entries to syslog server.
 */
public abstract class AbstractSocketWriter extends AbstractFormatPatternWriter {

	private static final String DEFAULT_HOST_NAME = "localhost";
	private static final int DEFAULT_PORT_NUMBER = 514;
	private static final String DEFAULT_FACILITY = "USER";
	private static final int FACILITY_CODE_SHIFT = 3;
	private static final String DEFAULT_SEVERITY = "INFORMATIONAL";

	private final InetAddress inetAddress;
	private final int port;
	private final Charset charset;
	private final String identification;

	/**
	 * @param properties
	 *            Configuration for writer
	 * 
	 * @throws UnknownHostException 
	 *            Host name cannot be identified
	 */
	public AbstractSocketWriter(final Map<String, String> properties) throws UnknownHostException {
		super(properties);

		String host = getStringValue("host");
		if (host == null) {
			host = DEFAULT_HOST_NAME;
		}
		inetAddress = InetAddress.getByName("localhost");
		String portNumber = getStringValue("port");
		if (portNumber == null) {
			port = DEFAULT_PORT_NUMBER;
		} else {
			port = Integer.parseInt(portNumber);
		}
		charset = super.getCharset();
		String identification = getStringValue("identification");
		if (identification == null) {
			this.identification = "";
		} else {
			this.identification = identification;
		}
	}

	public final InetAddress getInetAddress() {
		return inetAddress;
	}

	public final int getPort() {
		return port;
	}

	/**
	 * Return the priority code for facility and severity.
	 * 
	 * @param level
	 *            Log level to use when severity is not specified.
	 * 
	 * @return The priority code calculated from facility and severity code.
	 */
	public int getCode(final Level level) {
		String facility = getStringValue("facility");
		String severity = getStringValue("severity");

		if (facility == null) {
			facility = DEFAULT_FACILITY;
		}

		int facilityCode = SyslogFacility.valueOf(facility).getCode();

		if (severity == null) {
			if (level != null) {
				severity = SyslogSeverity.getSeverity(level).name();
			} else {
				severity = DEFAULT_SEVERITY;
			}
		}

		int severityCode = SyslogSeverity.valueOf(severity).getCode();

		return (facilityCode << FACILITY_CODE_SHIFT) + severityCode;
	}

	/**
	 * Return the formated syslog message. 
	 * 
	 * @param logEntry
	 *            Log entry for rendering.
	 * 
	 * @return The formated message.
	 */
	public byte[] formatMessage(final LogEntry logEntry) {
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(getCode(logEntry.getLevel()));
		builder.append(">");
		builder.append(identification);
		builder.append(": ");
		builder.append(render(logEntry));
		return builder.toString().getBytes(charset);
	}

}
