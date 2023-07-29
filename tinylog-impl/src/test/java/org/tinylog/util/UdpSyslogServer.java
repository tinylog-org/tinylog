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

package org.tinylog.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

public class UdpSyslogServer extends Thread {
	private final DatagramSocket socket;
	private volatile boolean shutdown;
	private Thread thread;
	private String lastMessage = "";

	public UdpSyslogServer(final int port) throws SocketException {
		this.socket = new DatagramSocket(port);
		this.shutdown = false;
	}

	@Override
	public void run() {
		final byte[] bytes = new byte[4096];
		this.thread = Thread.currentThread();
		final DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		try {
			while (!shutdown) {
				socket.receive(packet);
				lastMessage = new String(packet.getData(), 0, packet.getLength(), Charset.defaultCharset()).trim();
			}
		} catch (final IOException ex) {
			if (!shutdown) {
				InternalLogger.log(Level.ERROR, "Failed retrieving UDP message");
			}
		}
	}

	/**
	 * Shutdowns the server.
	 */
	public void shutdown() {
		shutdown = true;
		if (socket != null) {
			socket.close();
		}
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join(100);
			} catch (final InterruptedException ex) {
				InternalLogger.log(Level.ERROR, "UDP server thread shutdown failed.");
			}
		}
	}

	/**
	 * Return the last message received.
	 * 
	 * @return The last message.
	 */
	public String getLastMessage() {
		return lastMessage;
	}
}
