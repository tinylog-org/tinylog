module org.tinylog.api {
	requires static java.sql;

	exports org.tinylog;
	exports org.tinylog.configuration;
	exports org.tinylog.format;
	exports org.tinylog.provider;
	exports org.tinylog.runtime;
}
