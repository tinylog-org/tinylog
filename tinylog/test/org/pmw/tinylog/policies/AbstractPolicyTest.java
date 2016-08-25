/*
 * Copyright 2014 Martin Winandy
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

package org.pmw.tinylog.policies;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Properties;

import org.pmw.tinylog.AbstractTinylogTest;
import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.labelers.Labeler;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.util.ConfigurationCreator;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.util.PropertiesBuilder;
import org.pmw.tinylog.writers.Writer;
import org.pmw.tinylog.writers.PropertiesSupport;
import org.pmw.tinylog.writers.Property;

/**
 * Abstract test class for policies.
 * 
 * @see Policy
 */
public abstract class AbstractPolicyTest extends AbstractTinylogTest {

	/**
	 * Create a policy from properties.
	 * 
	 * @param property
	 *            Property value with policy definition
	 * @return Created policy
	 */
	protected final Policy createFromProperties(final String property) {
		try (ClassLoaderMock mock = new ClassLoaderMock((URLClassLoader) Labeler.class.getClassLoader())) {
			mock.set("META-INF/services/" + Writer.class.getPackage().getName(), PropertiesWriter.class.getName());
			Configurator configurator = ConfigurationCreator.getDummyConfigurator();
			PropertiesBuilder properties = new PropertiesBuilder().set("tinylog.writer", "properties").set("tinylog.writer.policy", property);

			Class<?> propertiesLoaderClass = Class.forName("org.pmw.tinylog.PropertiesLoader");
			Method readWriterMethod = propertiesLoaderClass.getDeclaredMethod("readWriters", Configurator.class, Properties.class);
			readWriterMethod.setAccessible(true);
			readWriterMethod.invoke(null, configurator, properties.create());

			Method createMethod = Configurator.class.getDeclaredMethod("create");
			createMethod.setAccessible(true);
			Configuration configuration = (Configuration) createMethod.invoke(configurator);
			Writer writer = configuration.getWriters().get(0);
			return writer instanceof PropertiesWriter ? ((PropertiesWriter) writer).policy : null;
		} catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	@PropertiesSupport(name = "properties", properties = { @Property(name = "policy", type = Policy.class, optional = true) })
	private static final class PropertiesWriter extends NullWriter {

		private final Policy policy;

		@SuppressWarnings("unused")
		public PropertiesWriter(final Policy policy) {
			this.policy = policy;
		}

	}

}
