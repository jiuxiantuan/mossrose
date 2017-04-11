package com.jiuxian.mossrose.test;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.JobOperation.JobRuntimeInfo;
import com.jiuxian.mossrose.MossroseProcess;
import com.jiuxian.mossrose.config.MossroseConfigFactory;

public class JobOperationTest {

	@Test
	public void test() throws Exception {
		MossroseProcess process = new MossroseProcess(MossroseConfigFactory.fromClasspathYamlFile("mossrose.yml"));
		process.run();

		JobOperation jobOperation = process.getJobOperation();

		List<JobRuntimeInfo> jobs = jobOperation.allJobs();
		// run a job now
		JobRuntimeInfo job = Iterables.getLast(jobs);
		jobOperation.runJobNow(job.getGroup(), job.getId());

		Thread.sleep(25 * 1000);

		jobOperation.allJobs().stream().forEach(System.out::println);

		try {
			Thread.sleep(60 * 60 * 1000);
		} catch (InterruptedException e) {
		}

	}

}
