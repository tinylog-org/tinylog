package org.tinylog.impl.policies;

import java.util.Locale;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of {@link SizePolicy}.
 */
public class SizePolicyBuilder implements PolicyBuilder {

	private static final long KB = 1024;
	private static final long MB = KB * 1024;
	private static final long GB = MB * 1024;

	/** */
	public SizePolicyBuilder() {
	}

	@Override
	public String getName() {
		return "size";
	}

	@Override
	public Policy create(Framework framework, String value) {
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("No maximum file size defined for size policy");
		}

		try {
			long size = parse(value.toUpperCase(Locale.ENGLISH));
			return new SizePolicy(size);
		} catch (RuntimeException ex) {
			throw new IllegalArgumentException("Invalid maximum file size \"" + value + "\" for size policy");
		}
	}

	/**
	 * Parses the file size from a string.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>2 KB -> 2048</code></pre>
	 * </p>
	 *
	 * @param value The string representation of a file size
	 * @return The file size in bytes
	 */
	private static long parse(String value) {
		if (value.endsWith("GB")) {
			return Long.parseLong(value.substring(0, value.length() - "GB".length()).trim()) * GB;
		} else if (value.endsWith("MB")) {
			return Long.parseLong(value.substring(0, value.length() - "MB".length()).trim()) * MB;
		} else if (value.endsWith("KB")) {
			return Long.parseLong(value.substring(0, value.length() - "KB".length()).trim()) * KB;
		} else {
			return Long.parseLong(value);
		}
	}

}
