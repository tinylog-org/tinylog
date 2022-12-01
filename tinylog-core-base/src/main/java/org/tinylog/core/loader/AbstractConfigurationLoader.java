package org.tinylog.core.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.core.variable.VariableResolver;

/**
 * Base class for configuration loader implementations.
 *
 * <p>
 *     It provides functions for loading files as {@link InputStream} and resolving variables in the loaded
 *     configuration map.
 * </p>
 */
public abstract class AbstractConfigurationLoader implements ConfigurationLoader {

    private static final Pattern URL_DETECTION_PATTERN = Pattern.compile("^[a-zA-Z]{2,}:/.*");

    /** */
    public AbstractConfigurationLoader() {
    }

    /**
     * Opens a URL, classpath resource, or local file as input stream.
     *
     * @param classLoader Class loader to use to open classpath resources
     * @param file URL, classpath resource, or local file
     * @return The input stream of the passed file
     * @throws IOException Failed to open the passed file
     */
    protected static InputStream getInputStream(ClassLoader classLoader, String file) throws IOException {
        if (URL_DETECTION_PATTERN.matcher(file).matches()) {
            return new URL(file).openStream();
        } else {
            InputStream stream = classLoader.getResourceAsStream(file);
            return stream == null ? new FileInputStream(file) : stream;
        }
    }

    /**
     * Resolves all variables in the passed configuration map.
     *
     * @param framework The actual framework instance
     * @param configuration The map with the loaded tinylog configuration
     */
    protected static void resolveVariables(Framework framework, Map<String, String> configuration) {
        List<VariableResolver> resolvers = SafeServiceLoader.asList(
            framework.getClassLoader(),
            VariableResolver.class,
            "variable resolvers"
        );

        for (VariableResolver resolver : resolvers) {
            String prefix = resolver.getPrefix();
            String regex = Pattern.quote(prefix) + "\\{([^|{}]+)(?:\\|([^{}]+))?\\}";
            Pattern pattern = Pattern.compile(regex);

            for (Map.Entry<String, String> entry : configuration.entrySet()) {
                String value = entry.getValue();
                if (value.contains(prefix)) {
                    Matcher matcher = pattern.matcher(value);
                    StringBuffer buffer = new StringBuffer();
                    while (matcher.find()) {
                        matcher.appendReplacement(buffer, resolveVariable(matcher, resolver));
                    }
                    matcher.appendTail(buffer);
                    configuration.put(entry.getKey(), buffer.toString());
                }
            }
        }
    }

    /**
     * Resolves a found variable.
     *
     * @param matcher The actual matcher at a found variable placeholder
     * @param resolver The resolver to resolve the found variable placeholder
     * @return The replacement text for the found variable placeholder
     */
    private static String resolveVariable(Matcher matcher, VariableResolver resolver) {
        String name = matcher.group(1).trim();
        String value = resolver.resolve(name);

        if (value == null) {
            value = matcher.group(2);
            if (value == null) {
                InternalLogger.warn(
                    null,
                    "{}{} \"{}\" could not be found",
                    resolver.getName().substring(0, 1).toUpperCase(Locale.ENGLISH),
                    resolver.getName().substring(1),
                    name
                );
                value = matcher.group();
            } else {
                value = value.trim();
            }
        }

        return value;
    }

}
