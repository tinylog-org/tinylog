package org.tinylog.core.runtime;

/**
 * Predicate for testing values that allows throwing exception of other kind of throwables.
 *
 * @param <T> Supported value type
 */
@FunctionalInterface
interface FailableCheck<T> {

	/**
	 * Verifies if the passed value is valid.
	 *
	 * @param value Value to verify
	 * @return {@code true} if the passed value is valid, {@code false} if not
	 * @throws Throwable Any exception or other kind of throwable can be thrown and has to be handles by the caller
	 */
	boolean test(T value) throws Throwable;

}
