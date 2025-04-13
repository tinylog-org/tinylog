package org.tinylog.impl.policy;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public Policy create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for startup policy: \"{}\"",
                value
            );
        }

        return new StartupPolicy();
    }

}
