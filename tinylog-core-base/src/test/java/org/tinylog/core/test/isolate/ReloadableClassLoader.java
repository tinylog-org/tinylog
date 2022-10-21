package org.tinylog.core.test.isolate;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class loader that reloads defined classes whenever these classes are requested.
 */
public class ReloadableClassLoader extends URLClassLoader {

	private final Collection<String> reloadableClassNames;

	/**
	 * @param reloadableClasses The classes that should be reloaded everytime
	 * @param parent The parent class loader is used for loading all other classes
	 */
	public ReloadableClassLoader(Collection<Class<?>> reloadableClasses, ClassLoader parent) {
		super(getLocations(reloadableClasses), parent);
		reloadableClassNames = stripClassNames(reloadableClasses);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			if (reloadableClassNames.contains(stripClassName(name))) {
				Class<?> clazz = findLoadedClass(name);
				if (clazz == null) {
					clazz = findClass(name);
				}
				if (resolve) {
					resolveClass(clazz);
				}
				return clazz;
			} else {
				return super.loadClass(name, resolve);
			}
		}
	}

	/**
	 * Gets all locations to the class files of the passed classes as URLs.
	 *
	 * @param classes The classes for which the locations should be returned
	 * @return All found class file locations
	 */
	private static URL[] getLocations(Collection<Class<?>> classes) {
		return classes.stream()
			.map(clazz -> clazz.getProtectionDomain().getCodeSource().getLocation())
			.distinct()
			.toArray(URL[]::new);
	}

	/**
	 * Gets the stripped fully-qualified class names of all passed classes. The root class name will be used for
	 * anonymous, nested or inner classes.
	 *
	 * @param classes The classes for which the stripped fully-qualified class name should be returned
	 * @return All stripped fully-qualified class names
	 */
	private static Set<String> stripClassNames(Collection<Class<?>> classes) {
		return classes.stream()
			.map(Class::getName)
			.map(ReloadableClassLoader::stripClassName)
			.collect(Collectors.toSet());
	}

	/**
	 * Gets the stripped fully-qualified class name of the passed class name. The root class name will be used for
	 * anonymous, nested or inner classes.
	 *
	 * @param className The class name for which the stripped fully-qualified class name should be returned
	 * @return The stripped fully-qualified class name
	 */
	private static String stripClassName(String className) {
		int indexDot = className.lastIndexOf('.');
		int indexDollar = className.indexOf('$', indexDot + 1);
		if (indexDollar >= 0) {
			return className.substring(0, indexDollar);
		} else {
			return className;
		}
	}

}
