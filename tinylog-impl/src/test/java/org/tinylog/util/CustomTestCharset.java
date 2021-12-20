/*
 * Copyright 2021 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * Custom charset that can read and write all ASCII characters. One defined character will be doubled to two bytes
 * instead of one for testing different lengths for characters.
 */
public class CustomTestCharset extends Charset {

	private final char longCharacter;

	/**
	 * @param longCharacter This character will take two bytes instead of one
	 */
	public CustomTestCharset(final char longCharacter) {
		super(String.format("CUSTOM-TEST-CHARSET-%02X", (short) longCharacter), new String[0]);
		this.longCharacter = longCharacter;
	}

	@Override
	public boolean contains(final Charset charset) {
		return false;
	}

	@Override
	public CharsetDecoder newDecoder() {
		return new CharsetDecoder(this, 1, 2) {
			@Override
			protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
				while (in.hasRemaining()) {
					if (!out.hasRemaining()) {
						return CoderResult.OVERFLOW;
					}

					byte character = in.get();

					if (character == longCharacter) {
						if (in.hasRemaining()) {
							character = in.get();
						} else {
							in.position(in.position() - 1);
							return CoderResult.UNDERFLOW;
						}
					}

					out.put((char) character);
				}

				return CoderResult.UNDERFLOW;
			}
		};
	}

	@Override
	public CharsetEncoder newEncoder() {
		return new CharsetEncoder(this, 1, 2) {
			@Override
			protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
				while (in.hasRemaining()) {
					char character = in.get();
					int length = character == longCharacter ? 2 : 1;

					if (out.remaining() < length) {
						in.position(in.position() - 1);
						return CoderResult.OVERFLOW;
					}

					for (int i = 0; i < length; ++i) {
						out.put((byte) character);
					}
				}

				return CoderResult.UNDERFLOW;
			}
		};
	}

}
