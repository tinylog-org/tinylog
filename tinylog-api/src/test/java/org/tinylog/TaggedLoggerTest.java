package org.tinylog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaggedLoggerTest {

	/**
	 * Verifies that a string can be assigned as tag.
	 */
	@Test
	void stringTag() {
		TaggedLogger logger = new TaggedLogger("dummy");
		assertThat(logger.getTag()).isEqualTo("dummy");
	}

	/**
	 * Verifies that {@code null} can be passed as tag for creating an untagged logger.
	 */
	@Test
	void nullTag() {
		TaggedLogger logger = new TaggedLogger(null);
		assertThat(logger.getTag()).isNull();
	}

}
