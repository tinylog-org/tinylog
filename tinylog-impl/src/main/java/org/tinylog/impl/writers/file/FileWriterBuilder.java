package org.tinylog.impl.writers.file;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.path.PathParser;
import org.tinylog.impl.path.segments.PathSegment;
import org.tinylog.impl.policies.BundlePolicy;
import org.tinylog.impl.policies.EndlessPolicy;
import org.tinylog.impl.policies.Policy;
import org.tinylog.impl.policies.PolicyBuilder;
import org.tinylog.impl.writers.AbstractFormattableWriterBuilder;
import org.tinylog.impl.writers.Writer;

/**
 * Builder for creating an instance of {@link FileWriter}.
 */
public class FileWriterBuilder extends AbstractFormattableWriterBuilder {

	private static final String FILE_KEY = "file";
	private static final String CHARSET_KEY = "charset";
	private static final String POLICIES_KEY = "policies";

	/** */
	public FileWriterBuilder() {
	}

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public Writer create(Framework framework, Configuration configuration, OutputFormat format) throws Exception {
		String fileName = configuration.getValue(FILE_KEY);
		if (fileName == null) {
			String fullKey = configuration.resolveFullKey(FILE_KEY);
			throw new IllegalArgumentException("File name is missing in required property \"" + fullKey + "\"");
		}

		PathSegment path = new PathParser(framework).parse(fileName);
		Charset charset = getCharset(configuration);
		Policy policy = getPolicy(framework, configuration);

		return new FileWriter(framework.getClock(), format, policy, path, charset);
	}

	/**
	 * Gets the charset for the {@link FileWriter}.
	 *
	 * @param configuration The file writer configuration
	 * @return The configured charset or UTF-8 is no charset is configured
	 */
	private Charset getCharset(Configuration configuration) {
		String charsetName = configuration.getValue(CHARSET_KEY);
		Charset charset = StandardCharsets.UTF_8;
		if (charsetName != null) {
			try {
				charset = Charset.forName(charsetName);
			} catch (IllegalArgumentException ex) {
				InternalLogger.error(
					ex,
					"Invalid charset \"{}\" in property \"{}\"",
					charsetName,
					configuration.resolveFullKey(CHARSET_KEY)
				);
			}
		}
		return charset;
	}

	/**
	 * Gets the policy for {@link FileWriter}.
	 *
	 * <p>
	 *     Multiple policies will be bundled into a single policy by using {@link BundlePolicy}.
	 * </p>
	 *
	 * @param framework The actual logging framework instance
	 * @param configuration The file writer configuration
	 * @return The configured policies or {@link EndlessPolicy} if no policies are configured
	 */
	private Policy getPolicy(Framework framework, Configuration configuration) {
		List<Policy> policies = new ArrayList<>();

		for (String policyConfiguration : configuration.getList(POLICIES_KEY)) {
			int index = policyConfiguration.indexOf(':');
			String name = index >= 0 ? policyConfiguration.substring(0, index).trim() : policyConfiguration;
			String value = index >= 0 ? policyConfiguration.substring(index + 1).trim() : null;

			PolicyBuilder builder = SafeServiceLoader
				.asList(framework, PolicyBuilder.class, "policy builders")
				.stream()
				.filter(policyBuilder -> name.equals(policyBuilder.getName()))
				.findAny()
				.orElse(null);

			if (builder == null) {
				InternalLogger.error(
					null,
					"Could not find any policy builder with the name \"{}\" in the classpath",
					name
				);
			} else {
				try {
					policies.add(builder.create(framework, value));
				} catch (Exception ex) {
					InternalLogger.error(ex, "Failed to create policy for \"{}\"", name);
				}
			}
		}

		if (policies.isEmpty()) {
			return new EndlessPolicy();
		} else if (policies.size() == 1) {
			return policies.get(0);
		} else {
			return new BundlePolicy(policies);
		}
	}

}
