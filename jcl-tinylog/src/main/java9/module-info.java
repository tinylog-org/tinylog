module org.tinylog.api.jcl {
	requires org.tinylog.api;
	requires commons.logging;

	exports org.tinylog.jcl;

	provides org.apache.commons.logging.LogFactory with
			org.tinylog.jcl.TinylogLogFactory;
}
