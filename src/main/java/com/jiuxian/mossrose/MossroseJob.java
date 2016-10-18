package com.jiuxian.mossrose;

import java.io.Serializable;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteRunnable;
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
		if (runInCluster) {
			ignite.compute().run(new IgniteRunnable() {
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					doExecute();
				}
			});
		} else {
			doExecute();
		}
	}

	private void doExecute() {
		if (simpleJob != null) {
			simpleJob.execute();
		}
		if (distributedJob != null) {
			List<Serializable> items = distributedJob.slice();
			if (items != null) {
				items.stream().parallel().forEach(e -> distributedJob.execute(e));
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
