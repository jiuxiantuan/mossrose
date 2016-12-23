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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MossroseConfigFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(MossroseConfigFactory.class);

	public static MossroseConfig fromClasspathYamlFile(String file) {
		LOGGER.info("Loading mossrose yml config file: {}", file);
		return new YamlConfigParser().fromClasspathFile(file);
	}

}
