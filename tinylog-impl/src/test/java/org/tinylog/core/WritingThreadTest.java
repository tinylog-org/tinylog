/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;
import org.tinylog.util.LogEntryBuilder;
import org.tinylog.writers.FileWriter;
import org.tinylog.writers.Writer;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests for {@link WritingThread}.
 */
public final class WritingThreadTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that a single log entry will be written.
	 *
	 * @throws Exception
	 *             Illegal thread or mock invocation
	 */
	@Test
	public void singleLogEntry() throws Exception {
		Writer writer = mock(Writer.class);
		LogEntry entry = LogEntryBuilder.empty().create();

		WritingThread thread = new WritingThread(singletonList(writer));
		thread.start();

		thread.add(writer, entry);
		Thread.sleep(100); // Wait for flushing

		thread.shutdown();
		thread.join();

		verify(writer).write(entry);
		verify(writer).flush();
		verify(writer).close();
	}

	/**
	 * Verifies that multiple threads can serve log entries simultaneously.
	 *
	 * @throws IOException
	 *             Failed creating or opening log file
	 * @throws InterruptedException
	 *             Failed waiting for a thread
	 */
	@Test
	public void multiThreaded() throws IOException, InterruptedException {
		String file = FileSystem.createTemporaryFile();
		Map<String, String> configuration = new HashMap<>();
		configuration.put("file", file);
		configuration.put("buffered", "true");
		configuration.put("format", "{message}");
		configuration.put("writingthread", "true");

		Writer writer = new FileWriter(configuration);
		LogEntry entry = LogEntryBuilder.empty().message("Hello World!").create();

		WritingThread writingThread = new WritingThread(singletonList(writer));
		writingThread.start();

		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 10; ++i) {
			threads.add(new Thread(() -> {
				for (int j = 0; j < 1000; ++j) {
					writingThread.add(writer, entry);
				}
			}));
		}

		threads.forEach(thread -> thread.start());

		for (Thread thread : threads) {
			thread.join();
		}

		writingThread.shutdown();
		writingThread.join();

		assertThat(Files.readAllLines(Paths.get(file))).hasSize(10 * 1000).containsOnly("Hello World!");
	}

	/**
	 * Verifies that a thrown exception will be reported while writing.
	 *
	 * @throws Exception
	 *             Illegal thread or mock invocation
	 */
	@Test
	public void failWriting() throws Exception {
		Writer writer = mock(Writer.class);
		doThrow(IOException.class).when(writer).write(any());
		LogEntry entry = LogEntryBuilder.empty().create();

		WritingThread thread = new WritingThread(singletonList(writer));
		thread.start();
		thread.add(writer, entry);
		thread.shutdown();
		thread.join();

		verify(writer).write(entry);
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce(IOException.class.getName());
		verify(writer).close();
	}

	/**
	 * Verifies that a thrown exception will be reported while flushing an underlying writer.
	 *
	 * @throws Exception
	 *             Illegal thread or mock invocation
	 */
	@Test
	public void failFlushing() throws Exception {
		Writer writer = mock(Writer.class);
		doThrow(IOException.class).when(writer).flush();

		WritingThread thread = new WritingThread(singletonList(writer));
		thread.start();

		thread.add(writer, LogEntryBuilder.empty().create());
		Thread.sleep(100); // Wait for flushing

		thread.shutdown();
		thread.join();

		verify(writer).flush();
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce(IOException.class.getName());
		verify(writer).close();
	}

	/**
	 * Verifies that a thrown exception will be reported while closing an underlying writer.
	 *
	 * @throws Exception
	 *             Illegal thread or mock invocation
	 */
	@Test
	public void failClosing() throws Exception {
		Writer writer = mock(Writer.class);
		doThrow(IOException.class).when(writer).close();

		WritingThread thread = new WritingThread(singletonList(writer));
		thread.start();
		thread.shutdown();
		thread.join();

		verify(writer).close();
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce(IOException.class.getName());
	}

}
