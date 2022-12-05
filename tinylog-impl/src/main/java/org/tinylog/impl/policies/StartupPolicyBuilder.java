package org.tinylog.impl.policies;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link StartupPolicy}.
 */
public class StartupPolicyBuilder implements PolicyBuilder {

    /** */
    public StartupPolicyBuilder() {
    }

    @Override
    public String getName() {
        return "startup";
    }

    @Override
    public Policy create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for startup policy: \"{}\"",
                value
            );
        }

        return new StartupPolicy();
    }

}
