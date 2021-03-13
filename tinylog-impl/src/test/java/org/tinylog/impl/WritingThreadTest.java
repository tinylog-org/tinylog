package org.tinylog.impl;

import java.util.Collections;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.writer.AsyncWriter;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@CaptureLogEntries
class WritingThreadTest {

	@Inject
	private Log log;

	/**
	 * Verifies that the writing thread starts and shuts down correctly.
	 */
	@Test
	void lifeCycle() throws InterruptedException {
		WritingThread thread = new WritingThread(Collections.emptyList(), 10);
		thread.start();

		try {
			assertThat(thread.isAlive()).isTrue();
		} finally {
			thread.shutdown();
			thread.join(1000);
			assertThat(thread.isAlive()).isFalse();
		}
	}

	/**
	 * Verifies that the writing thread writes log entries and flushes writers correctly.
	 */
	@Test
	void outputLogEntries() throws Exception {
		LogEntry firstLogEntry = new LogEntryBuilder().message("1").create();
		LogEntry secondLogEntry = new LogEntryBuilder().message("2").create();
		LogEntry thirdLogEntry = new LogEntryBuilder().message("3").create();

		AsyncWriter writer = mock(AsyncWriter.class);

		WritingThread thread = new WritingThread(Collections.singletonList(writer), 2);
		thread.start();
		try {
			thread.enqueue(writer, firstLogEntry);
			thread.enqueue(writer, secondLogEntry);
			thread.enqueue(writer, thirdLogEntry);
		} finally {
			thread.shutdown();
		}

		thread.join();

		InOrder inOrder = inOrder(writer);
		inOrder.verify(writer).log(firstLogEntry);
		inOrder.verify(writer).log(secondLogEntry);
		inOrder.verify(writer).log(thirdLogEntry);

		verify(writer, atLeast(1)).flush();
		verify(writer, atMost(2)).flush();
	}

	/**
	 * Verifies that internal tinylog log entries will be discarded, if the waiting queue is full, but all standard log
	 * entries will be output nevertheless.
	 */
	@Test
	void discardLogEntries() throws Exception {
		LogEntry standardLogEntry = new LogEntryBuilder().tag(null).message("Hello World!").create();
		LogEntry internalLogEntry = new LogEntryBuilder().tag("tinylog").message("internal").create();

		AsyncWriter writer = mock(AsyncWriter.class);

		WritingThread thread = new WritingThread(Collections.singletonList(writer), 3);
		thread.start();
		try {
			for (int i = 0; i < 100; ++i) {
				thread.enqueue(writer, standardLogEntry);
				thread.enqueue(writer, internalLogEntry);
			}
		} finally {
			thread.shutdown();
		}

		thread.join();

		verify(writer, times(100)).log(standardLogEntry);
		verify(writer, atLeast(1)).log(internalLogEntry);
		verify(writer, atMost(99)).log(internalLogEntry);
	}

	/**
	 * Verifies that thrown exceptions while outputting a log entry are reported but do not prevent the output of other
	 * log entries.
	 */
	@Test
	void reportLoggingException() throws Exception {
		LogEntry logEntry = new LogEntryBuilder().message("foo").create();
		AsyncWriter evilWriter = mock(AsyncWriter.class);
		AsyncWriter goodWriter = mock(AsyncWriter.class);

		doThrow(NullPointerException.class).when(evilWriter).log(any());

		WritingThread thread = new WritingThread(ImmutableList.of(evilWriter, goodWriter), 10);
		thread.start();
		try {
			thread.enqueue(evilWriter, logEntry);
			thread.enqueue(goodWriter, logEntry);
		} finally {
			thread.shutdown();
		}

		thread.join();

		verify(evilWriter).log(logEntry);
		verify(goodWriter).log(logEntry);

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).isInstanceOf(NullPointerException.class);
			assertThat(entry.getMessage()).contains("log entry");
		});
	}

	/**
	 * Verifies that thrown exceptions while outputting an internal tinylog log entry are ignored.
	 */
	@Test
	void ignoreLoggingException() throws Exception {
		LogEntry logEntry = new LogEntryBuilder().tag("tinylog").message("foo").create();
		AsyncWriter evilWriter = mock(AsyncWriter.class);
		AsyncWriter goodWriter = mock(AsyncWriter.class);

		doThrow(NullPointerException.class).when(evilWriter).log(any());

		WritingThread thread = new WritingThread(ImmutableList.of(evilWriter, goodWriter), 10);
		thread.start();
		try {
			thread.enqueue(evilWriter, logEntry);
			thread.enqueue(goodWriter, logEntry);
		} finally {
			thread.shutdown();
		}

		thread.join();

		verify(evilWriter).log(logEntry);
		verify(goodWriter).log(logEntry);
	}

	/**
	 * Verifies that thrown exceptions while flushing a writer are reported but do not prevent flushing other writers.
	 */
	@Test
	void reportFlushingException() throws Exception {
		LogEntry logEntry = new LogEntryBuilder().message("foo").create();
		AsyncWriter evilWriter = mock(AsyncWriter.class);
		AsyncWriter goodWriter = mock(AsyncWriter.class);

		doThrow(NullPointerException.class).when(evilWriter).flush();

		WritingThread thread = new WritingThread(ImmutableList.of(evilWriter, goodWriter), 2);
		thread.start();
		try {
			thread.enqueue(evilWriter, logEntry);
			thread.enqueue(goodWriter, logEntry);
		} finally {
			thread.shutdown();
		}

		thread.join();

		verify(evilWriter).flush();
		verify(goodWriter).flush();

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).isInstanceOf(NullPointerException.class);
			assertThat(entry.getMessage()).contains("flush");
		});
	}

}
