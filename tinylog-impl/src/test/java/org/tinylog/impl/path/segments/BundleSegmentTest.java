package org.tinylog.impl.path.segments;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BundleSegmentTest {

	/**
	 * Verifies that the bundle segment appends the passed child path segments to the passed string builder.
	 */
	@Test
	void resolve() throws Exception {
		StaticPathSegment fooSegment = new StaticPathSegment("foo");
		StaticPathSegment barSegment = new StaticPathSegment("bar");
		BundleSegment segment = new BundleSegment(Arrays.asList(fooSegment, barSegment));

		StringBuilder builder = new StringBuilder("logs/");
		segment.resolve(builder, ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC));

		assertThat(builder).asString().isEqualTo("logs/foobar");
	}

}
