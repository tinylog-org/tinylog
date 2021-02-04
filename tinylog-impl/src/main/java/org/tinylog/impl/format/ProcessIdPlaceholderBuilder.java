package org.tinylog.impl.format;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link ProcessIdPlaceholder ProcessIdPlaceholders}.
 */
public class ProcessIdPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ProcessIdPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "process-id";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for process ID placeholder: \"{}\"",
				value
			);
		}

		long processId = framework.getRuntime().getProcessId();
		return new ProcessIdPlaceholder(processId);
	}

}
