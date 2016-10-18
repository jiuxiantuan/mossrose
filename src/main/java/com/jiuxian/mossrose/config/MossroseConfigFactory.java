package com.jiuxian.mossrose.config;

public class MossroseConfigFactory {

	public static MossroseConfig fromClasspathYamlFile(String file) {
		return new YamlConfigParser().fromClasspathFile(file);
	}

}
