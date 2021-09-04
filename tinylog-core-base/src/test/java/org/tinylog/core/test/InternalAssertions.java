package org.tinylog.core.test;

import org.tinylog.core.backend.LevelVisibility;

/**
 * Custom assertions for testing tinylog data classes with AssertJ.
 */
public final class InternalAssertions {

	/** */
	private InternalAssertions() {
	}

	/**
	 * Creates a new instance of {@link LevelVisibilityAssert} for a {@link LevelVisibility}.
	 *
	 * @param actual The actual level visibility to test
	 * @return A customized object assert for the passed level visibility
	 */
	public static LevelVisibilityAssert assertThat(LevelVisibility actual) {
		return new LevelVisibilityAssert(actual);
	}

}
