module org.tinylog.api.slf4j {
	requires org.tinylog.api;
	requires org.slf4j;

	exports org.slf4j.impl;
	exports org.tinylog.slf4j;
}
