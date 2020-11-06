package org.tinylog.core.format.value;

import java.util.Locale;

/**
 * Builder for creating {@link ValueFormat ValueFormats}.
 *
 * <p>
 *     New value format builders can be provided as {@link java.util.ServiceLoader service} in
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
