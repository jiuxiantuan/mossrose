package com.jiuxian.mossrose.test;

import org.junit.Assert;
import org.junit.Test;

import com.jiuxian.mossrose.config.ConfigParser;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.config.YamlConfigParser;

public class YamlConfigParserTest {

	@Test
	public void test() {
		ConfigParser parser = new YamlConfigParser();
		MossroseConfig config = parser.fromClasspathFile("mossrose.yaml");

		Assert.assertNotNull(config);

		Assert.assertEquals("zn-job", config.getCluster().getName());

		Assert.assertEquals(1, config.getJobs().size());

		JobMeta jobMeta = config.getJobs().get(0);
		Assert.assertEquals("1", jobMeta.getId());
		Assert.assertEquals("0/5 * * * * ?", jobMeta.getCron());
		Assert.assertEquals("com.jiuxian.mossrose.test.SomeJob", jobMeta.getMain());
	}

}
