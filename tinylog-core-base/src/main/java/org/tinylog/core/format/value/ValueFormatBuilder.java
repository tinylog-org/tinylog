package org.tinylog.core.format.value;

import java.util.Locale;

/**
 * Builder for creating an instance of a {@link ValueFormat}.
 *
 * <p>
 *     New value format builders can be provided as {@link java.util.ServiceLoader service} via
 *     {@code META-INF/services}.
 * </p>
 */
public interface ValueFormatBuilder {

    /**
     * Creates a new instance of the value format.
     *
     * @param locale Locale for language or country depending format outputs
     * @return New instance of the value format
     */
    ValueFormat create(Locale locale);

}
