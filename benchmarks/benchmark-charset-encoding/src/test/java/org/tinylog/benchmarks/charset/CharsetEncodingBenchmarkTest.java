package org.tinylog.benchmarks.charset;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CharsetEncodingBenchmarkTest {

	/**
	 * Verifies that {@link CharsetEncodingBenchmark#getBytes()} encodes {@link CharsetEncodingBenchmark#getContent()}
	 * as expected.
	 *
	 * @param charset The name of the charset to use for encoding
	 */
	@ParameterizedTest
	@ValueSource(strings = { "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16" })
	void getBytes(String charset) {
		CharsetEncodingBenchmark benchmark = new CharsetEncodingBenchmark(charset);
		benchmark.init();

		byte[] data = benchmark.getBytes();
		assertThat(data).asString(Charset.forName(charset)).isEqualTo(benchmark.getContent());
	}

	/**
	 * Verifies that {@link CharsetEncodingBenchmark#encode()} encodes {@link CharsetEncodingBenchmark#getContent()}
	 * as expected.
	 *
	 * @param charset The name of the charset to use for encoding
	 */
	@ParameterizedTest
	@ValueSource(strings = { "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16" })
	void encode(String charset) {
		CharsetEncodingBenchmark benchmark = new CharsetEncodingBenchmark(charset);
		benchmark.init();

		CoderResult result = benchmark.encode();
		assertThat(result.isError()).isFalse();
		assertThat(result.isMalformed()).isFalse();
		assertThat(result.isOverflow()).isFalse();

		ByteBuffer buffer = benchmark.getBuffer();
		byte[] data = new byte[buffer.position()];
		buffer.rewind();
		buffer.get(data);
		assertThat(data).asString(Charset.forName(charset)).isEqualTo(benchmark.getContent());
	}

}
