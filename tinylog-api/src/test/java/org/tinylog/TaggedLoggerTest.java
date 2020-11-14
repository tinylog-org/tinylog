package org.tinylog;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class TaggedLoggerTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that a string can be assigned as tag.
	 */
	@Test
	void stringTag() {
		TaggedLogger logger = new TaggedLogger("dummy", framework);
		assertThat(logger.getTag()).isEqualTo("dummy");
	}

	/**
	 * Verifies that {@code null} can be passed as tag for creating an untagged logger.
	 */
	@Test
	void nullTag() {
		TaggedLogger logger = new TaggedLogger(null, framework);
		assertThat(logger.getTag()).isNull();
	}

}
