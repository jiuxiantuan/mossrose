/**
 * Copyright 2015-2020 jiuxian.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose.config;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Throwables;

public class YamlConfigParser implements ConfigParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(YamlConfigParser.class);

	@Override
	public MossroseConfig fromClasspathFile(String file) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		MossroseConfig config = null;
		try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(file)) {
			if (in == null) {
				throw new RuntimeException("Mossrose config file " + file + " cannot be found.");
			}
			config = mapper.readValue(in, MossroseConfig.class);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}
		return config;
	}

}
