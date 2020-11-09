package org.tinylog.core.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;

/**
 * Base extension class for custom parameterized JUnit5 extensions.
 */
abstract class AbstractParameterizedExtension extends AbstractExtension implements ParameterResolver {

	private final Map<Class<?>, Function<ExtensionContext, ?>> parameters;

	/** */
	AbstractParameterizedExtension() {
		parameters = new HashMap<>();
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Class<?> type = parameterContext.getParameter().getType();
		return parameters.containsKey(type);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Class<?> type = parameterContext.getParameter().getType();
		Function<ExtensionContext, ?> producer = parameters.get(type);
		if (producer == null) {
			throw new IllegalStateException("Unexpected parameter type: " + type.getName());
		} else {
			return producer.apply(extensionContext);
		}
	}

	/**
	 * Registers a supported parameter that can be set by this extension.
	 *
	 * @param type The parameter type
	 * @param producer Supplier function to produce a value for this parameter
	 * @param <T> The generic parameter type
	 */
	protected <T> void registerParameter(Class<T> type, Function<ExtensionContext, T> producer) {
		parameters.put(type, producer);
	}

	/**
	 * Finds all registered annotations.
	 *
	 * @param context The extension context
	 * @param annotationClass The annotation class to search for
	 * @param <A> The generic annotation class
	 * @return All found annotations
	 */
	protected <A extends Annotation> List<A> findAnnotations(ExtensionContext context, Class<A> annotationClass) {
		List<A> annotations = new ArrayList<>();

		context.getTestInstances().ifPresent(instances -> {
			for (Object object : instances.getAllInstances()) {
				A annotation = object.getClass().getAnnotation(annotationClass);
				if (annotation != null) {
					annotations.add(annotation);
				}
			}
		});

		context.getTestMethod().ifPresent(method -> {
			A annotation = method.getAnnotation(annotationClass);
			if (annotation != null) {
				annotations.add(annotation);
			}
		});

		return annotations;
	}

	/**
	 * Sets the passed value to all fields with {@link javax.inject.Inject} annotation and matching value type.
	 *
	 * @param context The current extension context
	 * @param value The value to inject (must be not {@code null})
	 * @param <T> The value type
	 * @throws IllegalAccessException Failed to set the value
	 */
	protected <T> void injectFields(ExtensionContext context, T value) throws IllegalAccessException {
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

}
