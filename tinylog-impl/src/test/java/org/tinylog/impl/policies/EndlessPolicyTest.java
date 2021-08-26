package org.tinylog.impl.policies;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EndlessPolicyTest {

	/**
	 * Verifies that a log file can be continued without accessing the log file itself.
	 */
	@Test
	void canContinueFile() {
		EndlessPolicy policy = new EndlessPolicy();
		assertThat(policy.canContinueFile(null)).isTrue();
	}

	/**
	 * Verifies that log entries of any size are accepted without accessing the log file.
	 */
	@Test
	void canAcceptLogEntry() {
		EndlessPolicy policy = new EndlessPolicy();
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(Integer.MAX_VALUE)).isTrue();
	}

}
