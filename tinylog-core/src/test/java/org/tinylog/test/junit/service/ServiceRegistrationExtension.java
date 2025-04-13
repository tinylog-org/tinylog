package org.tinylog.test.junit.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.AbstractParameterizedExtension;

/**
 * JUnit extension for registering service implementations for {@link ServiceLoader}.
 *
 * <p>
 *     Use the annotation {@link RegisterService} to apply this extension.
 * </p>
 */
public class ServiceRegistrationExtension extends AbstractParameterizedExtension implements BeforeEachCallback,
    AfterEachCallback {

    /** */
    public ServiceRegistrationExtension() {
        registerParameter(ClassLoader.class, this::getClassLoader);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws IOException, IllegalAccessException {
        Path temporaryFolder = Files.createTempDirectory(null);
        Path serviceFolder = temporaryFolder.resolve("META-INF").resolve("services");
        Files.createDirectories(serviceFolder);

        for (RegisterService annotation : findAnnotations(context, RegisterService.class)) {
            String content = Arrays.stream(annotation.implementations())
                .map(Class::getName)
                .collect(Collectors.joining("\n"));
            Path file = serviceFolder.resolve(annotation.service().getName());
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            Files.write(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        ClassLoader defaultLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader wrappedLoader = new WrappedClassLoader(temporaryFolder, defaultLoader);

        put(context, Path.class, temporaryFolder);
        put(context, ClassLoader.class, wrappedLoader);

        injectFields(context, getClassLoader(context));
        reinjectFields(context, TinylogContext.class, this::convertTinylogContext);

        Thread.currentThread().setContextClassLoader(wrappedLoader);
        temporaryFolder.toFile().deleteOnExit();
    }

    @Override
    public void afterEach(ExtensionContext context) throws IOException {
        ClassLoader loader = get(context, ClassLoader.class);
        if (loader != null) {
            Thread.currentThread().setContextClassLoader(loader);
        }

        Path folder = get(context, Path.class);
        if (folder != null) {
            for (Path path : Files.walk(folder).sorted(Comparator.reverseOrder()).toArray(Path[]::new)) {
                Files.deleteIfExists(path);
            }
        }
    }

    /**
     * Gets the wrapped {@link ClassLoader} instance from the store.
     *
     * @param context The current extension context
     * @return The {@link ClassLoader} instance from the store
     */
    private ClassLoader getClassLoader(ExtensionContext context) {
        return get(context, ClassLoader.class);
    }

    /**
     * Recreates a passed tinylog context with the wrapped class loader.
     *
     * @param extensionContext The current extension context
     * @param tinylogContext The current tinylog context
     * @return The new updated tinylog context
     */
    private TinylogContext convertTinylogContext(ExtensionContext extensionContext, TinylogContext tinylogContext) {
        if (tinylogContext == null) {
            return null;
        } else {
            return new TinylogContext(
                getClassLoader(extensionContext),
                tinylogContext.getClock(),
                tinylogContext.getRuntime(),
                tinylogContext.getConfiguration(),
                tinylogContext.getLogger()
            );
        }
    }

}
