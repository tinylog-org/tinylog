package org.tinylog.core.test.service;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.tinylog.core.test.AbstractExtension;

/**
 * JUnit extension for registering service implementations for {@link ServiceLoader}.
 *
 * <p>
 *     Use the annotation {@link RegisterService} to apply this extension.
 * </p>
 */
public class ServiceRegistrationExtension extends AbstractExtension {

    /** */
    public ServiceRegistrationExtension() {
    }

    @Override
    public void beforeEach(ExtensionContext context) throws IOException {
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

        URL[] urls = new URL[] {temporaryFolder.toUri().toURL()};
        ClassLoader defaultLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader wrappedLoader = new URLClassLoader(urls, defaultLoader);

        put(context, Path.class, temporaryFolder);
        put(context, ClassLoader.class, defaultLoader);

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

}
