package org.tinylog.impl.policies;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link StartupPolicy StartupPolicies}.
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
	public Policy create(Framework framework, String value) {
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
