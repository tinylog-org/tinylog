/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.benchmarks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

/**
 * Data class for representing a logging framework.
 */
public final class Framework {

	private static final Pattern JAR_VERSION = Pattern.compile(".*-([\\d.]+(-.*)?)\\.jar");
	private static final Pattern POM_VERSION = Pattern.compile("<parent>.*<version>(\\S+)</version>.*</parent>", Pattern.DOTALL);

	private final String name;
	private final String async;
	private final String version;

	/**
	 * @param name
	 *            Human-readable name
	 */
	public Framework(final String name) {
		this.name = name;
		this.async = null;
		this.version = null;
	}

	/**
	 * @param name
	 *            Human-readable name
	 * @param async
	 *            Description for asynchronous execution
	 * @param logger
	 *            Logger class of the framework
	 */
	public Framework(final String name, final String async, final Class<?> logger) {
		this.name = name;
		this.async = async;
		this.version = getVersion(logger);
	}

	/**
	 * Gets the human-readable name of the framework. The version will be included if available.
	 *
	 * @param async
	 *            {@code true} for appending the async suffix to the name,
	 *            {@code false} for receiving the name without the async suffix
	 * @return Human-readable name of the framework
	 */
	public String getName(final boolean async) {
		String versionedName = version == null ? name : name + " " + version;
		return async ? versionedName + " " + this.async : versionedName;
	}

	/**
	 * Gets the version for a class.
	 * 
	 * @param clazz
	 *            Class for which the version should be determined
	 * @return Found version or {@code null}
	 */
	private static String getVersion(final Class<?> clazz) {
		CodeSource source = clazz.getProtectionDomain().getCodeSource();
		URL location = source.getLocation();

		try {
			Path path = Paths.get(location.toURI());
			return Files.isRegularFile(path) ? getVersionFromJar(path) : getVersionFromPom(path);
		} catch (URISyntaxException ex) {
			Logger.error(ex, "Unknown location: \"{}\"", location);
			return null;
		}
	}

	/**
	 * Gets the version for a JAR file.
	 * 
	 * @param path
	 *            Path to JAR file
	 * @return Found version or {@code null}
	 */
	private static String getVersionFromJar(final Path path) {
		Matcher matcher = JAR_VERSION.matcher(path.toString());
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			Logger.error("JAR file \"{}\" does not contain a version", path);
			return null;
		}
	}

	/**
	 * Gets the version for a folder by searching for a pom.xml in the folder and its folders.
	 * 
	 * @param path
	 *            Path to a folder
	 * @return Found version or {@code null}
	 */
	private static String getVersionFromPom(final Path path) {
		Path folder = path;
		Path file;

		while (true) {
			file = folder.resolve("pom.xml");
			if (Files.exists(file)) {
				break;
			}

			folder = folder.getParent();
			if (folder == null) {
				Logger.error("pom.xml is missing for \"{}\"", path);
				return null;
			}
		}

		String content;
		try {
			content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			Logger.error(ex, "Failed reading \"{}\"", path);
			return null;
		}

		Matcher matcher = POM_VERSION.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			Logger.error("POM \"{}\" does not contain a version", file);
			return null;
		}
	}

}
