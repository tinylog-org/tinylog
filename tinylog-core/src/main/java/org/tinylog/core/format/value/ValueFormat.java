package org.tinylog.core.format.value;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.tinylog.core.Configuration;

/**
 * Format interface for different value types.
 */
public interface ValueFormat {

    /**
     * Checks if the passed value is supported.
     *
     * @param value The value to test
     * @return {@code true} if the passed value is supported, {@code false} if not
     */
    boolean isSupported(Object value);

    /**
     * Formats the passed value.
     *
     * @param configuration The current tinylog configuration
     * @param pattern The format pattern for the value
     * @param value The value to format
     * @return The formatted value
     */
    String format(Configuration configuration, String pattern, Object value);

    /**
     * Loads and creates all available value formats.
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A list with all value formats
     */
    static List<ValueFormat> load(ClassLoader loader) {
        List<ValueFormat> formats = new ArrayList<>();
        ServiceLoader.load(ValueFormat.class, loader).forEach(formats::add);
        return formats;
    }

}
