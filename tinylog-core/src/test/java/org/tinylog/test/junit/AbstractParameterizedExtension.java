package org.tinylog.test.junit;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;

import jakarta.inject.Inject;

/**
 * Base extension class for parameterized JUnit extensions.
 */
public abstract class AbstractParameterizedExtension extends AbstractExtension implements ParameterResolver {

    private final Map<Class<?>, Function<ExtensionContext, ?>> parameters;

    /** */
    protected AbstractParameterizedExtension() {
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
     * Registers a supported parameter that can be handled by this extension.
     *
     * @param type The parameter type
     * @param producer Supplier function to produce a valid value for the parameter
     * @param <T> The generic parameter type
     */
    protected <T> void registerParameter(Class<T> type, Function<ExtensionContext, T> producer) {
        parameters.put(type, producer);
    }

    /**
     * Sets the passed value to all fields that are annotated with {@link Inject} and have a compatible value type.
     *
     * @param context The current extension context
     * @param value The value to inject (must not be {@code null})
     * @param <T> The value type
     * @throws IllegalAccessException If failed to set the value
     */
    protected <T> void injectFields(ExtensionContext context, T value) throws IllegalAccessException {
        for (Object object : getTestInstances(context)) {
            for (Field field : getInjectableFields(object, value.getClass())) {
                field.setAccessible(true);
                field.set(object, value);
            }
        }
    }

    /**
     * Updates all fields that are annotated with {@link Inject} and have a compatible value type by executing the
     * passed conversion function.
     *
     * @param context The current extension context
     * @param type The value type
     * @param convertFunction The function for converting the currently present value
     * @param <T> The value type
     * @throws IllegalAccessException If failed to set the new value
     */
    @SuppressWarnings("unchecked")
    protected <T> void reinjectFields(
        ExtensionContext context,
        Class<T> type,
        BiFunction<ExtensionContext, T, T> convertFunction
    ) throws IllegalAccessException {
        for (Object object : getTestInstances(context)) {
            for (Field field : getInjectableFields(object, type)) {
                field.setAccessible(true);
                T oldValue = (T) field.get(object);
                T newValue = convertFunction.apply(context, oldValue);
                field.set(object, newValue);
            }
        }
    }

    /**
     * Gets all present test instances.
     *
     * @param context The current extension context
     * @return All test instances
     */
    private static List<Object> getTestInstances(ExtensionContext context) {
        Optional<TestInstances> instances = context.getTestInstances();
        if (instances.isPresent()) {
            return instances.get().getAllInstances();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets all injectable fields of a given type for an object.
     *
     * @param object The object instance
     * @param type The expected field type
     * @param <T> The expected field type
     * @return All found fields
     */
    private static <T> List<Field> getInjectableFields(Object object, Class<T> type) {
        return AnnotationSupport.findAnnotatedFields(
            object.getClass(),
            Inject.class,
            field -> field.getType().isAssignableFrom(type),
            HierarchyTraversalMode.TOP_DOWN
        );
    }

}
