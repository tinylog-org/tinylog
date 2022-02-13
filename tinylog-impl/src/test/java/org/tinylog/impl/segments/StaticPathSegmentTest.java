package org.tinylog.impl.segments;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticPathSegmentTest {

	/**
	 * Verifies that the static path segment appends the stored text data to the passed string builder.
	 */
	@Test
	void resolve() {
		StringBuilder builder = new StringBuilder("bar/");
		new StaticPathSegment("foo").resolve(builder, null);
		assertThat(builder).asString().isEqualTo("bar/foo");
	}

}
