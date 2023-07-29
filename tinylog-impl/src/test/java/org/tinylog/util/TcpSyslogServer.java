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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

public class TcpSyslogServer extends Thread {
	private final ServerSocket serverSocket;
	private volatile boolean shutdown;
	private Thread thread;
	private String lastMessage = "";

	public TcpSyslogServer(final int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.shutdown = false;
	}

	@Override
	public void run() {
		final byte[] bytes = new byte[4096];
		this.thread = Thread.currentThread();
		try {
			Socket socket = serverSocket.accept();
			while (!shutdown) {
				int len = socket.getInputStream().read(bytes, 0, bytes.length);
				if (len != -1) {
					lastMessage = new String(bytes, 0, len, Charset.defaultCharset()).trim();
				}
			}
		} catch (final IOException ex) {
			if (!shutdown) {
				InternalLogger.log(Level.ERROR, "Failed retrieving TCP message");
			}
		}
	}

	public void shutdown() throws IOException {
		shutdown = true;
		if (serverSocket != null) {
			serverSocket.close();
		}
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join(100);
			} catch (final InterruptedException ex) {
				InternalLogger.log(Level.ERROR, "TCP server thread shutdown failed.");
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
