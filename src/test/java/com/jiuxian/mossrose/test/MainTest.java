package com.jiuxian.mossrose.test;

import org.junit.Test;

import com.jiuxian.mossrose.MasterProcess;
import com.jiuxian.mossrose.cluster.ZookeeperClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfigFactory;
import com.jiuxian.theone.Guard;
import com.jiuxian.theone.Process;
import com.jiuxian.theone.SimpleSingleLane;
import com.jiuxian.theone.zk.ZookeeperGuard;

public class MainTest {

	@Test
	public void test() {
		String name = "djob";
		String zks = "192.168.5.99,192.168.5.104";
		Guard guard = new ZookeeperGuard(zks, 5 * 1000);
		Process process = new MasterProcess(MossroseConfigFactory.fromClasspathYamlFile("mossrose.yaml"), new ZookeeperClusterDiscovery("/mossrose/jobtest", zks));
		new SimpleSingleLane(name, guard, process).compete();

		try {
			Thread.sleep(60 * 60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
