package org.tinylog.impl.policy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StartupPolicyTest {

	/**
	 * Verifies that a log file will be discontinued without accessing the log file itself.
	 */
	@Test
	void canContinueFile() {
		StartupPolicy policy = new StartupPolicy();
		assertThat(policy.canContinueFile(null)).isFalse();
	}

	/**
	 * Verifies that log entries of any size are accepted without accessing the log file.
	 */
	@Test
	void canAcceptLogEntry() {
		StartupPolicy policy = new StartupPolicy();
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(Integer.MAX_VALUE)).isTrue();
	}

}
