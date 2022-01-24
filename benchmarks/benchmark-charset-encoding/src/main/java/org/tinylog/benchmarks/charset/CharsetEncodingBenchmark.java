package org.tinylog.benchmarks.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Benchmark for comparing the performance of different approaches for encoding strings.
 */
@State(Scope.Thread)
public class CharsetEncodingBenchmark {

	private static final int MAX_BYTES_PER_CHAR = 4;

	private static final String CONTENT = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy"
		+ " eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam"
		+ " et justo duo dolores.";

	@Param({ "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16" })
	private String charsetName;

	private Charset charset;
	private CharsetEncoder encoder;
	private ByteBuffer buffer;

	/** */
	public CharsetEncodingBenchmark() {
	}

	/**
	 * @param charsetName The name of the charset to use for encoding
	 */
	public CharsetEncodingBenchmark(String charsetName) {
		this.charsetName = charsetName;
	}

	/**
	 * Gets the text that is used for encoding.
	 *
	 * @return The text to encode
	 */
	public String getContent() {
		return CONTENT;
	}

	/**
	 * Gets the buffer that is used by {@link #encode()} for storing the encoded text.
	 *
	 * @return The buffer for encoded text
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Initializes the member fields.
	 */
	@Setup(Level.Trial)
	public void init() {
		charset = Charset.forName(charsetName);
		encoder = charset.newEncoder();
		buffer = ByteBuffer.allocate(CONTENT.length() * MAX_BYTES_PER_CHAR);
	}

	/**
	 * Uses {@link String#getBytes(Charset)} for encoding.
	 *
	 * @return The byte array for the encoded string
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public byte[] getBytes() {
		return CONTENT.getBytes(charset);
	}

	/**
	 * Uses {@link CharsetEncoder#encode(CharBuffer, ByteBuffer, boolean)} for encoding.
	 *
	 * @return The coder result for the encoded string
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public CoderResult encode() {
		buffer.rewind();
		return encoder.encode(CharBuffer.wrap(CONTENT), buffer, true);
	}

}
