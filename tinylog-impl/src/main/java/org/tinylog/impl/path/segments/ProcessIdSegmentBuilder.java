package org.tinylog.impl.path.segments;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of {@link StaticPathSegment} with the process ID of the current process.
 */
public class ProcessIdSegmentBuilder implements PathSegmentBuilder {

	/** */
	public ProcessIdSegmentBuilder() {
	}

	@Override
	public String getName() {
		return "process-id";
	}

	@Override
	public PathSegment create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for process ID path segment: \"{}\"",
				value
			);
		}

		long processId = framework.getRuntime().getProcessId();
		return new StaticPathSegment(Long.toString(processId));
	}

}
