/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LoggingBackend;

/**
 * JUnit extension for capturing output log entries.
 *
 * <p>
 *     Use the annotation {@link CaptureLogEntries} to apply this extension.
 * </p>
 */
public class LogCaptureExtension implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

	private static final Namespace NAMESPACE = Namespace.create(LogCaptureExtension.class);

	/** */
	public LogCaptureExtension() {
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Class<?> type = parameterContext.getParameter().getType();
		return type == Framework.class || type == Log.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Class<?> type = parameterContext.getParameter().getType();
		if (type == Framework.class) {
			return getOrCreateFramework(extensionContext);
		} else if (type == Log.class) {
			return getOrCreateLog(extensionContext);
		} else {
			throw new IllegalStateException("Unexpected parameter type: " + type.getName());
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) throws IllegalAccessException {
		Framework framework = getOrCreateFramework(context);
		injectFields(context, framework);

		Log log = getOrCreateLog(context);
		injectFields(context, log);

		Configuration configuration = framework.getConfiguration();
		List<CaptureLogEntries> annotations = new ArrayList<>();

		context.getTestInstances().ifPresent(instances -> {
			for (Object object : instances.getAllInstances()) {
				CaptureLogEntries annotation = object.getClass().getAnnotation(CaptureLogEntries.class);
				if (annotation != null) {
					annotations.add(annotation);
				}
			}
		});

		context.getTestMethod().ifPresent(method -> {
			CaptureLogEntries annotation = method.getAnnotation(CaptureLogEntries.class);
			if (annotation != null) {
				annotations.add(annotation);
			}
		});

		annotations.stream().flatMap(annotation -> Arrays.stream(annotation.configuration())).forEach(entry -> {
			int index = entry.indexOf('=');
			if (index >= 0) {
				String key = entry.substring(0, index);
				String value = entry.substring(index + 1);
				configuration.set(key, value);
			}
		});

		if (annotations.isEmpty()) {
			log.setMinLevel(Level.WARN);
		} else {
			log.setMinLevel(annotations.get(annotations.size() - 1).minLevel());
		}

		if (annotations.isEmpty() || annotations.get(annotations.size() - 1).autostart()) {
			Level minLevel = log.getMinLevel();
			try {
				log.setMinLevel(Level.WARN);
				framework.startUp();
			} finally {
				log.setMinLevel(minLevel);
			}
		}
	}

	@Override
	public void afterEach(ExtensionContext context) {
		try {
			Framework framework = getOrCreateFramework(context);
			Log log = getOrCreateLog(context);

			Level minLevel = log.getMinLevel();
			try {
				log.setMinLevel(Level.WARN);
				framework.shutDown();
			} finally {
				log.setMinLevel(minLevel);
			}

			Assertions
				.assertThat(getOrCreateLog(context).consume())
				.as("Log should be empty after JUnit test")
				.isEmpty();
		} finally {
			remove(context, Framework.class);
			remove(context, CaptureLoggingBackend.class);
			remove(context, Log.class);
		}
	}

	/**
	 * Gets the actual {@link Framework} instance from the store. If there is no {@link Framework} present in the store,
	 * a new {@link Framework} will be created and added to the store.
	 *
	 * @param context The current extension context
	 * @return The {@link Framework} instance from the store
	 */
	private Framework getOrCreateFramework(ExtensionContext context) {
		return context.getStore(NAMESPACE).getOrComputeIfAbsent(
			Framework.class,
			type -> new Framework(false, false) {
				@Override
				protected LoggingBackend createLoggingBackend() {
					return getOrCreateLoggingBackend(context);
				}
			},
			Framework.class
		);
	}

	/**
	 * Gets the actual {@link CaptureLoggingBackend} instance from the store. If there is no
	 * {@link CaptureLoggingBackend} present in the store, a new {@link CaptureLoggingBackend} will be created and added
	 * to the store.
	 *
	 * @param context The current extension context
	 * @return The {@link CaptureLoggingBackend} instance from the store
	 */
	private CaptureLoggingBackend getOrCreateLoggingBackend(ExtensionContext context) {
		return context.getStore(NAMESPACE).getOrComputeIfAbsent(
			CaptureLoggingBackend.class,
			type -> new CaptureLoggingBackend(getOrCreateLog(context)),
			CaptureLoggingBackend.class
		);
	}

	/**
	 * Gets the actual {@link Log} instance from the store. If there is no {@link Log} present in the store, a new
	 * {@link Log} will be created and added to the store.
	 *
	 * @param context The current extension context
	 * @return The {@link Log} instance from the store
	 */
	private Log getOrCreateLog(ExtensionContext context) {
		return context.getStore(NAMESPACE).getOrComputeIfAbsent(
			Log.class,
			type -> new Log(),
			Log.class
		);
	}

	/**
	 * Sets the passed value to all fields with {@link Inject} annotation and matching value type.
	 *
	 * @param context The current extension context
	 * @param value The value to inject (must be not {@code null})
	 * @param <T> Value type
	 * @throws IllegalAccessException Failed to set the value
	 */
	private <T> void injectFields(ExtensionContext context, T value) throws IllegalAccessException {
		Optional<TestInstances> instances = context.getTestInstances();
		if (instances.isPresent()) {
			for (Object object : instances.get().getAllInstances()) {
				List<Field> fields = AnnotationSupport.findAnnotatedFields(
					object.getClass(),
					Inject.class,
					field -> field.getType().isAssignableFrom(value.getClass()),
					HierarchyTraversalMode.TOP_DOWN
				);

				for (Field field : fields) {
					field.setAccessible(true);
					field.set(object, value);
				}
			}
		}
	}

	/**
	 * Removes a value from the store if present.
	 *
	 * @param context The current extension context
	 * @param key The key of the value to remove form the store
	 */
	private void remove(ExtensionContext context, Class<?> key) {
		if (context.getStore(NAMESPACE).remove(key) == null) {
			context.getParent().ifPresent(parent -> remove(parent, key));
		}
	}

}
