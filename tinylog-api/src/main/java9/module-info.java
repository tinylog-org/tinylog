module org.tinylog.api {
	uses org.tinylog.provider.LoggingProvider;

	requires static java.sql;
	requires java.management;
	requires java.naming;

	exports org.tinylog;
	exports org.tinylog.configuration;
	exports org.tinylog.format;
	exports org.tinylog.provider;
	exports org.tinylog.runtime;
}
