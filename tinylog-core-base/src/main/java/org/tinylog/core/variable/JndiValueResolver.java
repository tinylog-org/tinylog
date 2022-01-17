package org.tinylog.core.variable;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.tinylog.core.internal.InternalLogger;

/**
 * Resolver for JNDI values.
 */
public class JndiValueResolver implements VariableResolver {

	private static final String DEFAULT_PREFIX = "java:comp/env/";

	/** */
	public JndiValueResolver() {
	}

	@Override
	public String getName() {
		return "JNDI value";
	}

	@Override
	public String getPrefix() {
		return "@";
	}

	@Override
	public String resolve(String name) {
		String fullName = name.contains(":") ? name : DEFAULT_PREFIX + name;

		try {
			Object value = InitialContext.doLookup(fullName);
			return value == null ? null : value.toString();
		} catch (NameNotFoundException ex) {
			return null;
		} catch (NamingException ex) {
			InternalLogger.error(ex, "Failed to look up \"{}\"", fullName);
			return null;
		}
	}

}
