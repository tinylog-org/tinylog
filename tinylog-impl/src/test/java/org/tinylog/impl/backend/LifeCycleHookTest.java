package org.tinylog.impl.backend;

import java.io.IOException;
import java.util.Collections;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Hook;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.writers.Writer;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@CaptureLogEntries
class LifeCycleHookTest {

	@Inject
	private Log log;

	/**
	 * Verifies that multiple writers can be closed successfully.
	 */
	@Test
	void shutDownWritersSuccessfully() throws Exception {
		Writer first = mock(Writer.class);
		Writer second = mock(Writer.class);

		Hook hook = new LifeCycleHook(ImmutableList.of(first, second), null);
		hook.startUp();
		hook.shutDown();

		verify(first).close();
		verify(second).close();
	}

	/**
	 * Verifies that other writers can be closed successfully, if failed closing a writer.
	 */
	@Test
	void shutDownWritersUnsuccessfully() throws Exception {
		Writer first = mock(Writer.class);
		Writer second = mock(Writer.class);
		Writer third = mock(Writer.class);

		doThrow(IOException.class).when(second).close();

		Hook hook = new LifeCycleHook(ImmutableList.of(first, second, third), null);
		hook.startUp();
		hook.shutDown();

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).isInstanceOf(IOException.class);
		});

		verify(first).close();
		verify(third).close();
	}

	/**
	 * Verifies that a writing thread can be shut down successfully.
	 */
	@Test
	void shutDownWritingThreadSuccessfully() {
		WritingThread writingThread = mock(WritingThread.class);
		Hook hook = new LifeCycleHook(Collections.emptyList(), writingThread);

		hook.startUp();
		verify(writingThread).start();

		hook.shutDown();
		verify(writingThread).shutDown();
	}

	/**
	 * Verifies that a writing thread can be interrupted while shutting down.
	 */
	@Test
	void shutDownWritingThreadUnsuccessfully() throws InterruptedException {
		WritingThread writingThread = mock(WritingThread.class);
		doThrow(InterruptedException.class).when(writingThread).join();

		Hook hook = new LifeCycleHook(Collections.emptyList(), writingThread);

		hook.startUp();
		verify(writingThread).start();

		hook.shutDown();
		verify(writingThread).shutDown();

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).isInstanceOf(InterruptedException.class);
		});
	}

}
