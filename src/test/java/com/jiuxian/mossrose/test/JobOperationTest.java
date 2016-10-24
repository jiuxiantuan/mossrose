package com.jiuxian.mossrose.test;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.JobOperation.JobRuntimeInfo;
import com.jiuxian.mossrose.cluster.ZookeeperClusterDiscovery;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.IgniteGridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfigFactory;
import com.jiuxian.mossrose.quartz.QuartzProcess;
import com.jiuxian.theone.zk.ZookeeperUniqueProcess;

public class JobOperationTest {

	@Test
	public void test() throws Exception {
		String zks = "192.168.5.99,192.168.5.104"; // zookeeper集群地址
		MossroseConfig mossroseConfig = MossroseConfigFactory.fromClasspathYamlFile("mossrose.yaml");
		GridComputer gridComputer = new IgniteGridComputer(mossroseConfig.getCluster(),
				new ZookeeperClusterDiscovery(mossroseConfig.getCluster().getName(), zks));
		QuartzProcess quartzProcess = new QuartzProcess(mossroseConfig, gridComputer);

		try (ZookeeperUniqueProcess process = new ZookeeperUniqueProcess(quartzProcess, zks)) {
			process.run();

			JobOperation jobOperation = quartzProcess;
			List<JobRuntimeInfo> jobs = jobOperation.allJobs();

			// run a job now
			JobRuntimeInfo job = Iterables.getLast(jobs);
			jobOperation.runJobNow(job.getGroup(), job.getId());

			Thread.sleep(25 * 1000);

			jobOperation.allJobs().stream().forEach(e -> System.out.println(e));
		}

	}

}
