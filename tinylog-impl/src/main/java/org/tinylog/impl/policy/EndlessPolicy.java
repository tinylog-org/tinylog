package org.tinylog.impl.policy;

import java.nio.file.Path;

/**
 * Endless policy that never triggers any rollover event.
 */
public class EndlessPolicy implements Policy {

	/** */
	public EndlessPolicy() {
	}

	@Override
	public boolean canContinueFile(Path file) {
		return true;
	}

	@Override
	public void init(Path file) {
		// Ignore
	}

	@Override
	public boolean canAcceptLogEntry(int bytes) {
		return true;
	}

}
