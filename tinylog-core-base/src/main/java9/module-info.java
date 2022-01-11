import org.tinylog.core.Hook;
import org.tinylog.core.format.value.DateFormatBuilder;
import org.tinylog.core.format.value.JavaTimeFormatBuilder;
import org.tinylog.core.format.value.NumberFormatBuilder;
import org.tinylog.core.format.value.ValueFormatBuilder;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.InternalLoggingBackendBuilder;
import org.tinylog.core.backend.NopLoggingBackendBuilder;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.loader.PropertiesLoader;
import org.tinylog.core.runtime.AndroidRuntimeBuilder;
import org.tinylog.core.runtime.JavaRuntimeBuilder;
import org.tinylog.core.runtime.RuntimeBuilder;
import org.tinylog.core.variable.EnvironmentVariableResolver;
import org.tinylog.core.variable.SystemPropertyResolver;
import org.tinylog.core.variable.VariableResolver;

module org.tinylog.core {

	requires java.management;

	uses Hook;

	uses LoggingBackendBuilder;
	provides LoggingBackendBuilder with
		InternalLoggingBackendBuilder,
		NopLoggingBackendBuilder;

	uses ValueFormatBuilder;
	provides ValueFormatBuilder with
		DateFormatBuilder,
		JavaTimeFormatBuilder,
		NumberFormatBuilder;

	uses ConfigurationLoader;
	provides ConfigurationLoader with
		PropertiesLoader;

	uses VariableResolver;
	provides VariableResolver with
		EnvironmentVariableResolver,
		SystemPropertyResolver;

	uses RuntimeBuilder;
	provides RuntimeBuilder with
		AndroidRuntimeBuilder,
		JavaRuntimeBuilder;

	exports org.tinylog.core;
	exports org.tinylog.core.backend;
	exports org.tinylog.core.context;
	exports org.tinylog.core.format.message;
	exports org.tinylog.core.format.value;
	exports org.tinylog.core.internal;
	exports org.tinylog.core.loader;
	exports org.tinylog.core.runtime;
	exports org.tinylog.core.variable;

}
