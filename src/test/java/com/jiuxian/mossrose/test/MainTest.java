package com.jiuxian.mossrose.test;

import org.junit.Test;

import com.jiuxian.mossrose.MossroseProcess;
import com.jiuxian.mossrose.config.MossroseConfigFactory;

public class MainTest {

	@Test
	public void test() throws Exception {
		MossroseProcess process = new MossroseProcess(MossroseConfigFactory.fromClasspathYamlFile("mossrose.yml"));
		process.run();

		try {
			Thread.sleep(60 * 60 * 1000);
		} catch (InterruptedException e) {
		}
	}

}
