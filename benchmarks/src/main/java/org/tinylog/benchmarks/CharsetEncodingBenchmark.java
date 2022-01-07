package org.tinylog.benchmarks;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * Benchmark for comparing the performance of different approaches for encoding strings.
 */
public class CharsetEncodingBenchmark {

	/** */
	public CharsetEncodingBenchmark() {
	}

	/**
	 * Uses {@link String#getBytes(Charset)} for encoding.
	 *
	 * @param state Arguments and data for the benchmark
	 * @return The byte array for the encoded string
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public byte[] getBytes(CharsetState state) {
		return state.content.getBytes(state.charset);
	}

	/**
	 * Uses {@link CharsetEncoder#encode(CharBuffer, ByteBuffer, boolean)} for encoding.
	 *
	 * @param state Arguments and data for the benchmark
	 * @return The coder result for the encoded string
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public CoderResult encode(CharsetState state) {
		CoderResult result = state.encoder.encode(CharBuffer.wrap(state.content), state.buffer, true);
		state.buffer.rewind();
		return result;
	}

}
