package org.tinylog;

import org.junit.jupiter.api.Test;
import org.tinylog.core.test.system.CaptureSystemOutput;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureSystemOutput(excludes = "TINYLOG WARN:.*tinylog-impl\\.jar.*")
class LoggerTest {

	/**
	 * Verifies that the same logger instance is returned for the same tag.
	 */
	@Test
	void sameLoggerInstanceForSameTag() {
		TaggedLogger first = Logger.tag("foo");
		TaggedLogger second = Logger.tag("foo");
		assertThat(first).isNotNull().isSameAs(second);
	}

	/**
	 * Verifies that different logger instances are returned for different tags.
	 */
	@Test
	void differentLoggerInstanceForDifferentTag() {
		TaggedLogger first = Logger.tag("foo");
		TaggedLogger second = Logger.tag("boo");

		assertThat(first).isNotNull();
		assertThat(second).isNotNull();
		assertThat(first).isNotSameAs(second);
	}

	/**
	 * Verifies that the same untagged root logger is returned for {@code null} and empty tags.
	 */
	@Test
	void sameUntaggedRootLoggerForNullAndEmptyTags() {
		TaggedLogger nullTag = Logger.tag(null);
		TaggedLogger emptyTag = Logger.tag("");

		assertThat(nullTag).isNotNull();
		assertThat(nullTag.getTag()).isNull();
		assertThat(emptyTag).isNotNull();
		assertThat(emptyTag.getTag()).isNull();

		assertThat(nullTag).isSameAs(emptyTag);
	}

}
