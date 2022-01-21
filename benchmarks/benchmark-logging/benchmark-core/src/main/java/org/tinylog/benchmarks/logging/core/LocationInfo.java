package org.tinylog.benchmarks.logging.core;

/**
 * Location information details to log.
 */
public enum LocationInfo {

	/**
	 * No location information.
	 */
	NONE,

	/**
	 * Only class or category (depends on the actual logging framework).
	 */
	CLASS_OR_CATEGORY_ONLY,

	/**
	 * Full location information (class and method).
	 */
	FULL

}
