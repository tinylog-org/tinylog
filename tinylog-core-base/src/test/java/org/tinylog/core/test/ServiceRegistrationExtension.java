package org.tinylog.core.test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * JUnit extension for registering service implementations for {@link ServiceLoader}.
 *
 * <p>
 *     Use the annotation {@link RegisterService} to apply this extension.
 * </p>
 */
public class ServiceRegistrationExtension implements BeforeEachCallback, AfterEachCallback {

	private static final Namespace NAMESPACE = Namespace.create(ServiceRegistrationExtension.class);

	/** */
	public ServiceRegistrationExtension() {
	}

	@Override
	public void beforeEach(ExtensionContext context) throws IOException {
		Optional<RegisterService> annotation = AnnotationSupport.findAnnotation(
			context.getRequiredTestMethod(),
			RegisterService.class
		);

		if (annotation.isPresent()) {
			RegisterService configuration = annotation.get();

			Path temporaryFolder = Files.createTempDirectory(null);
			Path serviceFolder = temporaryFolder.resolve("META-INF").resolve("services");
			Files.createDirectories(serviceFolder);

			String content = Arrays.stream(configuration.implementations())
				.map(Class::getName)
				.collect(Collectors.joining("\n"));
			Path file = serviceFolder.resolve(configuration.service().getName());
			Files.write(file, content.getBytes(StandardCharsets.UTF_8));

			URL[] urls = new URL[] {temporaryFolder.toUri().toURL()};
			ClassLoader defaultLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader wrappedLoader = new URLClassLoader(urls, defaultLoader);

			Store store = context.getStore(NAMESPACE);
			store.put(Path.class, temporaryFolder);
			store.put(ClassLoader.class, defaultLoader);

			Thread.currentThread().setContextClassLoader(wrappedLoader);
			temporaryFolder.toFile().deleteOnExit();
		}
	}

	@Override
	public void afterEach(ExtensionContext context) throws IOException {
		Store store = context.getStore(NAMESPACE);

		ClassLoader loader = store.get(ClassLoader.class, ClassLoader.class);
		if (loader != null) {
			Thread.currentThread().setContextClassLoader(loader);
		}

		Path folder = store.get(Path.class, Path.class);
		if (folder != null) {
			for (Path path : Files.walk(folder).sorted(Comparator.reverseOrder()).toArray(Path[]::new)) {
				Files.deleteIfExists(path);
			}
		}
	}

}
