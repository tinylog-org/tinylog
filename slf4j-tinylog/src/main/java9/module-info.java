module org.tinylog.api.slf4j {
	requires org.tinylog.api;
	requires org.slf4j;

	provides org.slf4j.spi.SLF4JServiceProvider with
		org.tinylog.slf4j.TinylogSlf4jServiceProvider;

	exports org.slf4j.impl;
	exports org.tinylog.slf4j;
}
