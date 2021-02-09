package org.tinylog.impl.format.placeholder;

import java.time.Instant;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link TimestampPlaceholder TimestampPlaceholders}.
 */
public class TimestampPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public TimestampPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "timestamp";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if ("milliseconds".equals(value)) {
			return new TimestampPlaceholder(Instant::toEpochMilli);
		}

		if (value != null && !value.isEmpty() && !"seconds".equals(value)) {
			InternalLogger.warn(
				null,
				"Configuration value \"{}\" is an unsupported time unit, only \"seconds\" and \"milliseconds\""
					+ "are supported",
				value
			);
		}

		return new TimestampPlaceholder(Instant::getEpochSecond);
	}

}
