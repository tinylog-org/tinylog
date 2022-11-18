package org.tinylog.core.backend;

/**
 * Output requirement details for issuing log entries.
 */
public enum OutputDetails {

    /**
     * Output is completely disabled.
     */
    DISABLED,

    /**
     * Output is enabled, but no stack trace location information of the caller are required.
     */
    ENABLED_WITHOUT_LOCATION_INFORMATION,

    /**
     * Output is enabled and the class name of the caller is required.
     */
    ENABLED_WITH_CALLER_CLASS_NAME,

    /**
     * Output is enabled and all stack trace location information of the caller are required.
     */
    ENABLED_WITH_FULL_LOCATION_INFORMATION

}
