package org.tinylog.core.test;

import java.util.function.Function;

import org.assertj.core.api.AbstractObjectAssert;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;

import com.google.common.collect.ImmutableMap;

/**
 * Customized object assert with special assertions for {@link LevelVisibility}.
 */
public class LevelVisibilityAssert extends AbstractObjectAssert<LevelVisibilityAssert, LevelVisibility> {

	/**
	 * @param actual The actual level visibility under test
	 */
	public LevelVisibilityAssert(LevelVisibility actual) {
		super(actual, LevelVisibilityAssert.class);
	}

	/**
	 * Verifies that the passed severity level and all more severe levels are enabled and all other severity levels are
	 * disabled.
	 *
	 * @param level The expected severity level
	 * @return This assertion object
	 */
	public LevelVisibilityAssert isEnabledFor(Level level) {
		ImmutableMap.<Level, Function<LevelVisibility, Boolean>>of(
			Level.TRACE, LevelVisibility::isTraceEnabled,
			Level.DEBUG, LevelVisibility::isDebugEnabled,
			Level.INFO, LevelVisibility::isInfoEnabled,
			Level.WARN, LevelVisibility::isWarnEnabled,
			Level.ERROR, LevelVisibility::isErrorEnabled
		).forEach((methodLevel, method) -> {
			if (methodLevel.isAtLeastAsSevereAs(level) && !method.apply(actual)) {
				failWithMessage("Severity level <%s> should be enabled but is disabled", methodLevel);
			} else if (!methodLevel.isAtLeastAsSevereAs(level) && method.apply(actual)) {
				failWithMessage("Severity level <%s> should be disabled but is enabled", methodLevel);
			}
		});

		return this;
	}

}
