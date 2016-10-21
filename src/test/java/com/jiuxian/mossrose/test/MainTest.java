package com.jiuxian.mossrose.test;

import org.junit.Test;

import com.jiuxian.mossrose.MossroseProcess;
import com.jiuxian.mossrose.config.MossroseConfigFactory;

public class MainTest {

	@Test
	public void test() throws Exception {
		String zks = "192.168.5.99,192.168.5.104"; // zookeeper集群地址
		try (MossroseProcess process = new MossroseProcess(MossroseConfigFactory.fromClasspathYamlFile("mossrose.yaml"), zks)) {
			process.run();

			try {
				Thread.sleep(60 * 60 * 1000);
			} catch (InterruptedException e) {
			}
		}
	}

}
