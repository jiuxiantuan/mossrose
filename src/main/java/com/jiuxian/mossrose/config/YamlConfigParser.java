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
			config = mapper.readValue(in, MossroseConfig.class);
			config.validate();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}
		return config;
	}

}
