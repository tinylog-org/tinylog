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

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tinylog.writers.JsonWriterTest;

/**
 * Service factory for {@link CustomTestCharset}.
 */
public class CustomTestCharsetProvider extends CharsetProvider {

	/**
	 * All required charsets for {@link JsonWriterTest.IllegalCharsetTest}.
	 */
	public static final List<Charset> CHARSETS = Stream.of('\n', '\r', ' ', '\t', ',', '[', ']')
			.map(CustomTestCharset::new)
			.collect(Collectors.toList());

	/** */
	public CustomTestCharsetProvider() {
	}

	@Override
	public Iterator<Charset> charsets() {
		return CHARSETS.iterator();
	}

	@Override
	public Charset charsetForName(final String charsetName) {
		return CHARSETS.stream()
			.filter(charset -> Objects.equals(charsetName, charset.name()))
			.findFirst()
			.orElse(null);
	}
	
}
