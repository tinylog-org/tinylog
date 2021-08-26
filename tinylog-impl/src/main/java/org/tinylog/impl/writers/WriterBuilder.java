package org.tinylog.impl.writers;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;

/**
 * Builder for creating {@link Writer Writers}.
 *
 * <p>
 *     New writer builders can be provided as {@link java.util.ServiceLoader service} in {@code META-INF/services}.
 * </p>
 */
public interface WriterBuilder {

	/**
	 * Gets the name of the writer, which can be used to address the writer in a configuration.
	 *
	 * <p>
	 *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
	 *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
	 *     allowed.
	 * </p>
	 *
	 * @return The name of the writer
	 */
	String getName();

	/**
	 * Creates a new instance of the writer.
	 *
	 * <p>
	 *     Synchronous writers can implement the plain {@link Writer} interface and asynchronous writers the
	 *     {@link AsyncWriter} interface. Writers with blocking output operations (e.g. outputting log entries to files,
	 *     databases, or remote servers) should use the {@link AsyncWriter} interface for performance reasons.
	 * </p>
	 *
	 * @param framework The actual logging framework instance
	 * @param configuration The configuration properties for the new writer instance
	 * @return New instance of the writer
	 * @throws Exception Failed to create a new writer for the passed configuration
	 */
	Writer create(Framework framework, Configuration configuration) throws Exception;

}
