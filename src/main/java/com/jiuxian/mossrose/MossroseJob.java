package com.jiuxian.mossrose;

import java.io.Serializable;
import java.util.List;

import org.apache.ignite.Ignite;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.SimpleJob;

public class MossroseJob implements Job {

	private SimpleJob simpleJob;

	private DistributedJob<Serializable> distributedJob;

	private Ignite ignite;

	private boolean runInCluster;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (simpleJob != null) {
			if (runInCluster) {
				ignite.compute().run(() -> simpleJob.execute());
			} else {
				simpleJob.execute();
			}
		}
		if (distributedJob != null) {
			final List<Serializable> items = distributedJob.slice();
			if (items != null) {
				if (runInCluster) {
					items.stream().forEach(e -> ignite.compute().run(() -> distributedJob.execute(e)));
				} else {
					items.stream().parallel().forEach(e -> distributedJob.execute(e));
				}
			}
		}
	}

	public void setRunInCluster(boolean runInCluster) {
		this.runInCluster = runInCluster;
	}

	public void setSimpleJob(SimpleJob simpleJob) {
		this.simpleJob = simpleJob;
	}

	public void setIgnite(Ignite ignite) {
		this.ignite = ignite;
	}

	public void setDistributedJob(DistributedJob<Serializable> distributedJob) {
		this.distributedJob = distributedJob;
	}

}
