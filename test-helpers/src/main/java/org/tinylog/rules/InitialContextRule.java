/*
 * Copyright 2017 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.rules;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Rule for providing an implementation for {@link InitialContext} with basic methods for bindings and lookups.
 */
public class InitialContextRule implements TestRule {

	/** */
	public InitialContextRule() {
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new ContextStatement(base);
	}

	/**
	 * Context factory for {@link SimpleContext}.
	 */
	public static final class SimpleInitialContextFactory implements InitialContextFactory {

		private static volatile SimpleContext context;

		/** */
		public SimpleInitialContextFactory() {
		}

		@Override
		public Context getInitialContext(final Hashtable<?, ?> environment) {
			return context;
		}

	}

	/**
	 * JUnit statement that registers {@link SimpleInitialContextFactory} as initial context factory before running a
	 * test. As soon as a test is done, the initial context factory will be unregistered.
	 */
	private static final class ContextStatement extends Statement {

		private final Statement base;

		/**
		 * @param base
		 *            Base statement the contains the test
		 */
		private ContextStatement(final Statement base) {
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			SimpleInitialContextFactory.context = new SimpleContext();
			String property = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, SimpleInitialContextFactory.class.getName());
			try {
				base.evaluate();
			} finally {
				if (property == null) {
					System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
				} else {
					System.setProperty(Context.INITIAL_CONTEXT_FACTORY, property);
				}
				SimpleInitialContextFactory.context = null;
			}
		}

	}

	/**
	 * Simple context with basic methods for environment variables, bindings and lookups. The most other methods are not
	 * implemented and will throw an {@link UnsupportedOperationException}.
	 */
	private static final class SimpleContext implements Context {

		private final Map<Name, Object> values;
		private final Hashtable<Object, Object> environment;

		/** */
		private SimpleContext() {
			values = new HashMap<>();
			environment = new Hashtable<>();
		}

		@Override
		public Object lookup(final Name name) throws NamingException {
			synchronized (values) {
				if (values.containsKey(name)) {
					return values.get(name);
				} else {
					throw new NameNotFoundException();
				}
			}
		}

		@Override
		public Object lookup(final String name) throws NamingException {
			return lookup(new CompositeName(name));
		}

		@Override
		public void bind(final Name name, final Object obj) {
			synchronized (values) {
				values.put(name, obj);
			}
		}

		@Override
		public void bind(final String name, final Object obj) throws NamingException {
			bind(new CompositeName(name), obj);
		}

		@Override
		public void rebind(final Name name, final Object obj) throws NamingException {
			synchronized (values) {
				if (values.containsKey(name)) {
					values.put(name, obj);
				} else {
					throw new NameNotFoundException();
				}
			}
		}

		@Override
		public void rebind(final String name, final Object obj) throws NamingException {
			rebind(new CompositeName(name), obj);
		}

		@Override
		public void unbind(final Name name) {
			synchronized (values) {
				values.remove(name);
			}
		}

		@Override
		public void unbind(final String name) throws NamingException {
			unbind(new CompositeName(name));
		}

		@Override
		public void rename(final Name oldName, final Name newName) throws NamingException {
			synchronized (values) {
				if (values.containsKey(oldName)) {
					if (values.containsKey(newName)) {
						throw new NameAlreadyBoundException();
					} else {
						values.put(newName, values.remove(oldName));
					}
				} else {
					throw new NameNotFoundException();
				}
			}
		}

		@Override
		public void rename(final String oldName, final String newName) throws NamingException {
			rename(new CompositeName(oldName), new CompositeName(newName));
		}

		@Override
		public NamingEnumeration<NameClassPair> list(final Name name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public NamingEnumeration<NameClassPair> list(final String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public NamingEnumeration<Binding> listBindings(final Name name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public NamingEnumeration<Binding> listBindings(final String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void destroySubcontext(final Name name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void destroySubcontext(final String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Context createSubcontext(final Name name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Context createSubcontext(final String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object lookupLink(final Name name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object lookupLink(final String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public NameParser getNameParser(final Name name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public NameParser getNameParser(final String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Name composeName(final Name name, final Name prefix) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String composeName(final String name, final String prefix) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object addToEnvironment(final String propName, final Object propVal) {
			return environment.put(propName, propVal);
		}

		@Override
		public Object removeFromEnvironment(final String propName) {
			return environment.remove(propName);
		}

		@Override
		public Hashtable<?, ?> getEnvironment() {
			return new Hashtable<>(environment);
		}

		@Override
		public void close() {
		}

		@Override
		public String getNameInNamespace() {
			throw new UnsupportedOperationException();
		}

	}

}
