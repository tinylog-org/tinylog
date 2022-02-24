package org.tinylog.impl.path.segments;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Bundle of multiple child path segments.
 *
 * <p>
 *     This bundle segment combines the resolving of multiple child path segments. All child path segments are
 *     resolved and combined in the order in which they have been passed.
 * </p>
 */
public class BundleSegment implements PathSegment {

	private final List<PathSegment> segments;

	/**
	 * @param segments Child path segments
	 */
	public BundleSegment(List<PathSegment> segments) {
		this.segments = segments;
	}

	@Override
	public void resolve(StringBuilder pathBuilder, ZonedDateTime date) throws Exception {
		for (PathSegment segment : segments) {
			segment.resolve(pathBuilder, date);
		}
	}

}
