module org.tinylog.adapter.jul {
	requires java.logging;
	requires org.tinylog.api;

	exports org.tinylog.adapter.jul;

	provides org.tinylog.provider.LoggingProvider with
			org.tinylog.adapter.jul.JavaUtilLoggingProvider;
}
