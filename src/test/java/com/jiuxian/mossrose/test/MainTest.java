package com.jiuxian.mossrose.test;

import org.junit.Test;

import com.jiuxian.mossrose.MossroseProcess;
import com.jiuxian.mossrose.cluster.ZookeeperClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfigFactory;
import com.jiuxian.theone.Process;
import com.jiuxian.theone.zk.ZookeeperGuardProcess;

public class MainTest {

	@Test
	public void test() throws Exception {
		String zks = "192.168.5.99,192.168.5.104";
		try (Process process = new MossroseProcess(MossroseConfigFactory.fromClasspathYamlFile("mossrose.yaml"), new ZookeeperClusterDiscovery("/mossrose/jobtest", zks))) {
			try (ZookeeperGuardProcess zookeeperGuardProcess = new ZookeeperGuardProcess(process, zks)) {
				zookeeperGuardProcess.run();

				try {
					Thread.sleep(60 * 60 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
