package org.tinylog.impl.backend;

import java.util.Collection;

import org.tinylog.core.Hook;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.writers.Writer;

/**
 * Hook for closing writers and shutting writing thread down.
 */
class ShutdownHook implements Hook {

	private final Collection<Writer> writers;
	private final WritingThread writingThread;

	/**
	 * @param writers All writers to close when the framework ist shutting down
	 * @param writingThread The optional writing thread to shut down when the framework ist shutting down
	 */
	ShutdownHook(Collection<Writer> writers, WritingThread writingThread) {
		this.writers = writers;
		this.writingThread = writingThread;
	}

	@Override
	public void startUp() {
		// Nothing to do
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
