package org.tinylog.core.test.isolate;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.tinylog.core.test.AbstractExtension;

/**
 * JUnit extension for isolating classes.
 *
 * <p>
 *     Use the annotation {@link IsolatedExecution} to apply this extension.
 * </p>
 */
public class IsolateInvocationExtension extends AbstractExtension implements InvocationInterceptor {

    /** */
    public IsolateInvocationExtension() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void beforeEach(ExtensionContext context) {
        Set<Class> classes = findAnnotations(context, IsolatedExecution.class).stream()
            .flatMap(annotation -> Stream.of(annotation.classes()))
            .collect(Collectors.toSet());

        put(context, Collection.class, classes);
        put(context, Map.class, new HashMap());
    }

    @Override
    public void afterEach(ExtensionContext context) throws IOException {
        ReloadableClassLoader classLoader = get(context, ReloadableClassLoader.class);
        if (classLoader != null) {
            classLoader.close();
        }

        remove(context, Collection.class);
        remove(context, Map.class);
        remove(context, ReloadableClassLoader.class);
    }

    @Override
    public void interceptBeforeAllMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) {
        throw new UnsupportedOperationException(
            "@BeforeAll is incompatible with @IsolatedExecution, please use @BeforeEach instead"
        );
    }

    @Override
    public void interceptBeforeEachMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws Throwable {
        intercept(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptTestMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws Throwable {
        intercept(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptTestTemplateMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws Throwable {
        intercept(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptAfterEachMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws Throwable {
        intercept(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptAfterAllMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) {
        throw new UnsupportedOperationException(
            "@AfterAll is incompatible with @IsolatedExecution, please use @AfterEach instead"
        );
    }

    /**
     * Skips the original method invocation and invokes the method in an isolated class instead.
     *
     * @param invocation The original invocation
     * @param invocationContext The context of the original invocation
     * @param extensionContext The current extension context
     * @throws Throwable Failed to invoke the method
     */
    private void intercept(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws Throwable {
        invocation.skip();

        Object testInstance = getOrCreateTestInstance(invocationContext, extensionContext);
        String methodName = invocationContext.getExecutable().getName();
        Object[] arguments = invocationContext.getArguments().toArray();
        invokeMethod(testInstance, methodName, arguments);
    }

    /**
     * Gets the object instance of the test class. If an isolated test class instance has not yet been created for the
     * current test method, a new object instance is created.
     *
     * @param invocationContext The context of the original invocation
     * @param extensionContext The current extension context
     * @return The object instance of the isolated test class to use
     * @throws ReflectiveOperationException Failed to load a new isolated test class instance
     */
    private Object getOrCreateTestInstance(
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws ReflectiveOperationException {
        String className = invocationContext.getTargetClass().getName();
        return getOrCreateInstance(
            extensionContext,
            className,
            () -> forkTestInstance(invocationContext, extensionContext)
        );
    }

    /**
     * Creates a new object instance of the isolated test class.
     *
     * @param invocationContext The context of the original invocation
     * @param extensionContext The current extension context
     * @return A new object instance of the isolated test class
     * @throws ReflectiveOperationException Failed to load a new isolated test class instance
     */
    @SuppressWarnings("unchecked")
    private Object forkTestInstance(
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws ReflectiveOperationException {
        Class<?> testClass = invocationContext.getTargetClass();
        String className = testClass.getName();

        ReloadableClassLoader classLoader = getOrCreate(extensionContext, ReloadableClassLoader.class, () -> {
            Collection<Class<?>> isolatedClasses = new HashSet<Class<?>>(get(extensionContext, Collection.class));
            isolatedClasses.add(testClass);
            return new ReloadableClassLoader(isolatedClasses, testClass.getClassLoader());
        });

        Class<?> reloadedClass = classLoader.loadClass(className);
        return createInstance(extensionContext, reloadedClass);
    }

    /**
     * Gets the object instance of a class. If an isolated class instance has not yet been created for the current test
     * method, a new instance is created.
     *
     * @param context The current extension context
     * @param className The fully-qualified class name
     * @param supplier Creator for a new object instance
     * @return The object instance of the passed class name to use
     * @throws ReflectiveOperationException Failed to load a new isolated class instance
     */
    @SuppressWarnings("unchecked")
    private Object getOrCreateInstance(
        ExtensionContext context,
        String className,
        ObjectSupplier supplier
    ) throws ReflectiveOperationException {
        Map<String, Object> instances = get(context, Map.class);

        Object instance = instances.get(className);
        if (instance == null) {
            instance = supplier.create();
            instances.put(className, instance);
        }

        return instance;
    }

    /**
     * Creates a new object instance of the passed isolated class.
     *
     * @param context The current extension context
     * @param clazz The isolated class for which a new object instance should be created
     * @return A new object instance of the isolated class
     * @throws ReflectiveOperationException Failed to load a new isolated class instance
     */
    private Object createInstance(ExtensionContext context, Class<?> clazz) throws ReflectiveOperationException {
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        if (clazz.isAnnotationPresent(Nested.class)) {
            String className = constructor.getParameterTypes()[0].getName();
            Object parent = getOrCreateInstance(context, className, () -> {
                Class<?> type = clazz.getClassLoader().loadClass(className);
                return createInstance(context, type);
            });

            return constructor.newInstance(parent);
        } else {
            return constructor.newInstance();
        }
    }

    /**
     * Invokes a method on an object by passing the method name and the arguments.
     *
     * @param instance The object on which the method has to be invoked
     * @param methodName The name of the method to invoke
     * @param arguments The arguments to pass to the method
     * @throws Throwable Failed to invoke the method
     */
    private void invokeMethod(Object instance, String methodName, Object... arguments) throws Throwable {
        Method method = Stream.of(instance.getClass().getDeclaredMethods())
            .filter(declaredMethod -> methodName.equals(declaredMethod.getName()))
            .findFirst()
            .orElseThrow(() -> new NoSuchMethodException(methodName));

        try {
            method.setAccessible(true);
            method.invoke(instance, arguments);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

}
