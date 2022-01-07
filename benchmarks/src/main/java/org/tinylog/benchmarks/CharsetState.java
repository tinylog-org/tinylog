package org.tinylog.benchmarks;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Stateful arguments and data for {@link CharsetEncodingBenchmark}.
 */
@State(Scope.Thread)
public class CharsetState {

	private static final int MAX_BYTES_PER_CHAR = 4;

	/**
	 * The name of the charset.
	 */
	@Param({ "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16" })
	public String charsetName;

	/**
	 * The real charset object.
	 */
	public Charset charset;

	/**
	 * The encoder for the charset.
	 */
	public CharsetEncoder encoder;

	/**
	 * Reusable byte buffer (size is four times of the string length).
	 */
	public ByteBuffer buffer;

	/**
	 * The string to encode.
	 */
	public String content = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor"
		+ " invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo"
		+ " dolores.";

	/** */
	public CharsetState() {
	}

	/**
	 * Initializes the state object.
	 */
	@Setup(Level.Trial)
	public void setUp() {
		charset = Charset.forName(charsetName);
		encoder = charset.newEncoder();
		buffer = ByteBuffer.allocate(content.length() * MAX_BYTES_PER_CHAR);
	}

}
