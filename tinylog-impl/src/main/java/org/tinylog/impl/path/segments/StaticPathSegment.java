package org.tinylog.impl.path.segments;

import java.nio.file.Path;
import java.time.ZonedDateTime;

/**
 * Path segment for static text data.
 */
public class StaticPathSegment implements PathSegment {

	private final String data;

	/**
	 * @param data Static text data for this path segment
	 */
	public StaticPathSegment(String data) {
		this.data = data;
	}

	@Override
	public String findLatest(Path parentDirectory, String prefix) {
		return data;
	}

	@Override
	public void resolve(StringBuilder pathBuilder, ZonedDateTime date) {
		pathBuilder.append(data);
	}

}
