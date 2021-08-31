package org.tinylog.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevelTest {

	/**
	 * Verifies that the least severe level of two passed severity levels can be determined correctly.
	 */
	@Test
	void leastSevereLevel() {
		assertThat(Level.leastSevereLevel(Level.WARN, Level.DEBUG)).isEqualTo(Level.DEBUG);
		assertThat(Level.leastSevereLevel(Level.INFO, Level.INFO)).isEqualTo(Level.INFO);
		assertThat(Level.leastSevereLevel(Level.DEBUG, Level.WARN)).isEqualTo(Level.DEBUG);
	}

	/**
	 * Verifies that the most severe level of two passed severity levels can be determined correctly.
	 */
	@Test
	void mostSevereLevel() {
		assertThat(Level.mostSevereLevel(Level.WARN, Level.DEBUG)).isEqualTo(Level.WARN);
		assertThat(Level.mostSevereLevel(Level.INFO, Level.INFO)).isEqualTo(Level.INFO);
		assertThat(Level.mostSevereLevel(Level.DEBUG, Level.WARN)).isEqualTo(Level.WARN);
	}

}
