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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

import org.tinylog.core.LogEntry;

public class UdpSocketWriter extends AbstractSocketWriter {

	private DatagramSocket datagramSocket;
	
	public UdpSocketWriter(final Map<String, String> properties) throws IOException {
		super(properties);

		datagramSocket = new DatagramSocket();
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		byte[] b = formatMessage(logEntry);
		final DatagramPacket packet = new DatagramPacket(b, b.length, getInetAddress(), getPort());
		datagramSocket.send(packet);
	}

	@Override
	public void flush() throws Exception {
	}

	@Override
	public void close() throws Exception {
		datagramSocket.close();
	}

}
