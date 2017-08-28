/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.labelers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.pmw.tinylog.AbstractTinylogTest;
import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.util.ConfigurationCreator;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.writers.PropertiesSupport;
import org.pmw.tinylog.writers.Property;
import org.pmw.tinylog.writers.Writer;

/**
 * Abstract test class for labelers.
 *
 * @see Labeler
 */
public abstract class AbstractLabelerTest extends AbstractTinylogTest {

	/**
	 * Generate a backup file for a given log file.
	 *
	 * @param baseFile
	 *            Log file
	 * @param fileExtension
	 *            File extension of log file
	 * @param label
	 *            Label to include before file extension
	 * @return Backup file
	 */
	protected static File getBackupFile(final File baseFile, final String fileExtension, final String label) {
		String path = baseFile.getPath();
		if (fileExtension == null) {
			File file = new File(path + "." + label);
			file.deleteOnExit();
			return file;
		} else {
			File file = new File(path.substring(0, path.length() - fileExtension.length() - 1) + "." + label + "." + fileExtension);
			file.deleteOnExit();
			return file;
		}
	}

	/**
	 * Create a labeler from properties.
	 *
	 * @param property
	 *            Property value with labeler definition
	 * @return Created labeler
	 */
	protected final Labeler createFromProperties(final String property) {
		try (ClassLoaderMock mock = new ClassLoaderMock(Labeler.class.getClassLoader())) {
			mock.set("META-INF/services/" + Writer.class.getPackage().getName(), PropertiesWriter.class.getName());
			Configurator configurator = ConfigurationCreator.getDummyConfigurator();
			PropertiesBuilder properties = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.labeler", property);

			Class<?> propertiesLoaderClass = Class.forName("org.pmw.tinylog.PropertiesLoader");
			Method readWriterMethod = propertiesLoaderClass.getDeclaredMethod("readWriters", Configurator.class, Properties.class);
			readWriterMethod.setAccessible(true);
			readWriterMethod.invoke(null, configurator, properties.create());

			Method createMethod = Configurator.class.getDeclaredMethod("create");
			createMethod.setAccessible(true);
			Configuration configuration = (Configuration) createMethod.invoke(configurator);
			Writer writer = configuration.getWriters().get(0);
			return writer instanceof PropertiesWriter ? ((PropertiesWriter) writer).labeler : null;
		} catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	@PropertiesSupport(name = "properties", properties = { @Property(name = "labeler", type = Labeler.class, optional = true) })
	private static final class PropertiesWriter extends NullWriter {

		private final Labeler labeler;

		@SuppressWarnings("unused")
		PropertiesWriter(final Labeler labeler) {
			this.labeler = labeler;
		}

	}

}
