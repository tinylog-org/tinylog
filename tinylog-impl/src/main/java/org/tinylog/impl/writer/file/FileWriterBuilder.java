package org.tinylog.impl.writer.file;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.path.DynamicPath;
import org.tinylog.impl.policy.BundlePolicy;
import org.tinylog.impl.policy.EndlessPolicy;
import org.tinylog.impl.policy.Policy;
import org.tinylog.impl.policy.PolicyBuilder;
import org.tinylog.impl.writer.AbstractFormattableWriterBuilder;
import org.tinylog.impl.writer.Writer;

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
    public Writer create(TinylogContext context, OutputFormat format) throws Exception {
        Configuration configuration = context.getConfiguration();

        String fileName = configuration.getValue(FILE_KEY);
        if (fileName == null) {
            String fullKey = configuration.resolveFullKey(FILE_KEY);
            throw new IllegalArgumentException("File name is missing in required property \"" + fullKey + "\"");
        }

        Charset charset = getCharset(context);
        Policy policy = getPolicy(context);
        DynamicPath path = new DynamicPath(context, fileName);

        return new FileWriter(format, policy, path, charset);
    }

    /**
     * Gets the charset for the {@link FileWriter}.
     *
     * @param context The tinylog context to use for getting the configured charset
     * @return The configured charset, or UTF-8 if no charset is explicitly configured
     */
    private Charset getCharset(TinylogContext context) {
        Configuration configuration = context.getConfiguration();
        String charsetName = configuration.getValue(CHARSET_KEY);
        Charset charset = StandardCharsets.UTF_8;

        if (charsetName != null) {
            try {
                charset = Charset.forName(charsetName);
            } catch (IllegalArgumentException ex) {
                context.getLogger().log(
                    Level.ERROR,
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
     * Gets the policy for the {@link FileWriter}.
     *
     * <p>
     *     Multiple policies will be bundled into a single policy by using {@link BundlePolicy}.
     * </p>
     *
     * @param context The tinylog context to use for creating the policies
     * @return The configured policies, or {@link EndlessPolicy} if no policies are explicitly configured
     */
    private Policy getPolicy(TinylogContext context) {
        Configuration configuration = context.getConfiguration();
        InternalLogger logger = context.getLogger();

        List<Policy> policies = new ArrayList<>();

        for (String policyConfiguration : configuration.getList(POLICIES_KEY)) {
            int index = policyConfiguration.indexOf(':');
            String name = index >= 0 ? policyConfiguration.substring(0, index).trim() : policyConfiguration;
            String value = index >= 0 ? policyConfiguration.substring(index + 1).trim() : null;

            PolicyBuilder builder = PolicyBuilder.load(context.getLoader()).get(name);

            if (builder == null) {
                logger.log(
                    Level.ERROR,
                    "Could not find any policy builder with the name \"{}\" in the classpath",
                    name
                );
            } else {
                try {
                    policies.add(builder.create(context, value));
                } catch (Exception ex) {
                    logger.log(Level.ERROR, ex, "Failed to create policy for \"{}\"", name);
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
