package org.tinylog.test.junit.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;

/**
 * Class loader wrapper that replaces global resources with custom resources from a folder.
 */
public class WrappedClassLoader extends URLClassLoader {

    /**
     * @param folder A Folder with additional resources
     * @param defaultLoader The default class loader
     * @throws MalformedURLException If the passed folder path cannot be converted into a URL
     */
    public WrappedClassLoader(Path folder, ClassLoader defaultLoader) throws MalformedURLException {
        super(new URL[] {folder.toUri().toURL()}, defaultLoader);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> enumeration = findResources(name);
        if (enumeration.hasMoreElements()) {
            return enumeration;
        } else {
            return super.getResources(name);
        }
    }

}
