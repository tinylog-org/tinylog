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
	public boolean canContinueFile(Path path) {
		return true;
	}

	@Override
	public void init(Path path) {
		// Ignore
	}

	@Override
	public boolean canAcceptLogEntry(int bytes) {
		return true;
	}

}
