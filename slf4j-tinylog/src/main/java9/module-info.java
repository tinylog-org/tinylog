module org.tinylog.api {

	requires org.slf4j;
	requires org.tinylog.core;

	provides org.slf4j.spi.SLF4JServiceProvider with
		org.tinylog.slf4j.TinylogServiceProvider;

	exports org.tinylog.slf4j;

}
