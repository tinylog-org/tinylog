package org.tinylog.impl.backend;

import java.util.Collection;

import org.tinylog.core.Hook;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.writers.Writer;

/**
 * Hook for closing writers as well as starting a writing thread and shutting it down for the
 * {@link NativeLoggingBackend}.
 */
class LifeCycleHook implements Hook {

	private final Collection<Writer> writers;
	private final WritingThread writingThread;

	/**
	 * @param writers All writers to close when the framework ist shutting down
	 * @param writingThread The optional writing thread for handling its start and shutdown
	 */
	LifeCycleHook(Collection<Writer> writers, WritingThread writingThread) {
		this.writers = writers;
		this.writingThread = writingThread;
	}

	@Override
	public void startUp() {
		if (writingThread != null) {
			writingThread.start();
		}
	}

	@Override
	public void shutDown() {
		for (Writer writer : writers) {
			try {
				writer.close();
			} catch (Exception ex) {
				InternalLogger.error(ex, "Failed to close writer");
			}
		}

		if (writingThread != null) {
			writingThread.shutDown();
		}
	}

}
