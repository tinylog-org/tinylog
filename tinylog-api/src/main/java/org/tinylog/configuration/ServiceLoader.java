/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Alternative service loader that supports constructors with arguments in opposite to {@link java.util.ServiceLoader}.
 *
 * @param <T>
 *            Service interface
 */
public final class ServiceLoader<T> {

	private static final String SERVICE_PREFIX = "META-INF/services/";
	private static final Pattern SPLIT_PATTERN = Pattern.compile(" ");

	private final Class<? extends T> service;
	private final Class<?>[] argumentTypes;

	private final ClassLoader classLoader;
	private final Collection<String> classes;

	/**
	 * @param service
	 *            Service interface
	 * @param argumentTypes
	 *            Expected argument types for constructors
	 */
	public ServiceLoader(final Class<? extends T> service, final Class<?>... argumentTypes) {
		this.service = service;
		this.argumentTypes = argumentTypes;
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.classes = loadClasses(service);
	}

	/**
	 * Creates a defined service implementation. The name can be either the fully-qualified class name or the simplified
	 * acronym. The acronym is the class name without package and service suffix.
	 *
	 * <p>
	 * The acronym for <tt>org.tinylog.writers.RollingFileWriter</tt> is for example <tt>rolling file</tt>.
	 * </p>
	 *
	 * @param name
	 *            Acronym or class name of service implementation
	 * @param arguments
	 *            Arguments for constructor of service implementation
	 * @return A new instance of service or {@code null} if failed to create service
	 */
	public T create(final String name, final Object... arguments) {
		if (name.indexOf('.') == -1) {
			String expectingClassName = toSimpleClassName(name);
			for (String className : classes) {
				int split = className.lastIndexOf('.');
				String simpleClassName = split == -1 ? className : className.substring(split + 1);
				if (expectingClassName.equals(simpleClassName)) {
					return createInstance(className, arguments);
				}
			}

			InternalLogger.log(Level.ERROR, "Service implementation '" + name + "' not found");
			return null;
		} else {
			return createInstance(name, arguments);
		}
	}

	/**
	 * Creates all registered service implementations.
	 *
	 * @param arguments
	 *            Arguments for constructors of service implementations
	 * @return Instances of all service implementations
	 */
	public Collection<T> createAll(final Object... arguments) {
		Collection<T> instances = new ArrayList<T>(classes.size());

		for (String className : classes) {
			T instance = createInstance(className, arguments);
			if (instance != null) {
				instances.add(instance);
			}
		}

		return instances;
	}

	/**
	 * Loads all registered service class names.
	 *
	 * @param <T>
	 *            Service interface
	 * @param service
	 *            Service interface
	 * @return Class names
	 */
	private static <T> Collection<String> loadClasses(final Class<? extends T> service) {
		String name = SERVICE_PREFIX + service.getName();
		Enumeration<URL> urls;
		try {
			urls = ClassLoader.getSystemResources(name);
		} catch (IOException ex) {
			InternalLogger.log(Level.ERROR, "Failed loading services from '" + name + "'");
			return Collections.emptyList();
		}

		Collection<String> classes = new ArrayList<String>();

		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					line = line.trim();
					if (line.length() > 0 && line.charAt(0) != '#' && !classes.contains(line)) {
						classes.add(line);
					}
				}
			} catch (IOException ex) {
				InternalLogger.log(Level.ERROR, "Failed reading service resource '" + url + "'");
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ex) {
						// Ignore
					}
				}
			}
		}

		return classes;
	}

	/**
	 * Generates the simple class name from an acronym. A simple class name is the class name without package.
	 *
	 * <p>
	 * The acronym <tt>rolling file</tt>, for example, will be transformed into <tt>RollingFileWriter</tt> for the
	 * service interface <tt>Writer</tt>.
	 * </p>
	 *
	 * @param name
	 *            Simplified acronym
	 * @return Simple class without package
	 */
	private String toSimpleClassName(final String name) {
		StringBuilder builder = new StringBuilder(name.length());
		for (String token : SPLIT_PATTERN.split(name)) {
			if (!token.isEmpty()) {
				builder.append(Character.toUpperCase(token.charAt(0)));
				builder.append(token, 1, token.length());
			}
		}
		builder.append(service.getSimpleName());
		return builder.toString();
	}

	/**
	 * Creates a new instance of a class.
	 *
	 * @param className
	 *            Fully-qualified class name
	 * @param arguments
	 *            Arguments for constructor
	 * @return A new instance of given class or {@code null} if creation failed
	 */
	@SuppressWarnings("unchecked")
	private T createInstance(final String className, final Object... arguments) {
		try {
			Class<?> implementation = Class.forName(className, false, classLoader);
			if (service.isAssignableFrom(implementation)) {
				return (T) implementation.getDeclaredConstructor(argumentTypes).newInstance(arguments);
			} else {
				InternalLogger.log(Level.ERROR, "Class '" + className + "' does not implement servcie interface '" + service + "'");
			}
		} catch (ClassNotFoundException ex) {
			InternalLogger.log(Level.ERROR, "Service implementation '" + className + "' not found");
		} catch (NoSuchMethodException ex) {
			InternalLogger.log(Level.ERROR, "Service implementation '" + className + "' has no matching constructor");
		} catch (InstantiationException ex) {
			InternalLogger.log(Level.ERROR, "Service implementation '" + className + "' is not instantiable");
		} catch (IllegalAccessException ex) {
			InternalLogger.log(Level.ERROR, "Constructor of service implementation '" + className + "' is not accessible");
		} catch (IllegalArgumentException ex) {
			InternalLogger.log(Level.ERROR, "Illegal arguments for constructor of service implementation '" + className + "'");
		} catch (InvocationTargetException ex) {
			InternalLogger.log(Level.ERROR, ex.getTargetException(), "Failed creating service implementation '" + className + "'");
		}

		return null;
	}

}
