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

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.writers.raw.AbstractSocketWriter;
import org.tinylog.writers.raw.TcpSocketWriter;
import org.tinylog.writers.raw.UdpSocketWriter;

/**
 * Writer for outputting log entries to syslog server.
 */
public final class SyslogWriter extends AbstractFormatPatternWriter {

	private AbstractSocketWriter socketWriter;

	/**
	 * @param properties
	 *            Configuration for writer
	 *
	 * @throws IOException
	 *             Socket cannot be opened for write access
	 * @throws IllegalArgumentException
	 *             A property has an invalid value or is missing in configuration
	 */
	public SyslogWriter(final Map<String, String> properties) throws IllegalArgumentException, IOException {
		super(properties);

		String protocol = getStringValue("protocol");
		if (protocol == null) {
			throw new IllegalArgumentException("Missing protocol");
		} else if (protocol.toUpperCase(Locale.ROOT).equals("UDP")) {
			socketWriter = new UdpSocketWriter(properties);
		} else if (protocol.toUpperCase(Locale.ROOT).equals("TCP")) {
			socketWriter = new TcpSocketWriter(properties);
		} else {
			throw new IllegalArgumentException("Invalid protocol");
		}
	}

	@Override
	public void write(final LogEntry logEntry) throws Exception { 
		socketWriter.write(logEntry);
	}

	@Override
	public void flush() throws Exception {
		socketWriter.flush();
	}

	@Override
	public void close() throws Exception {
		socketWriter.close();
	}

}
