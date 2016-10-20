package com.jiuxian.mossrose;

import java.io.Serializable;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.SimpleJob;

public class MossroseJob implements Job {

	private SimpleJob simpleJob;

	private DistributedJob<Serializable> distributedJob;

	private GridComputer gridComputer;

	private boolean runInCluster;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (simpleJob != null) {
			if (runInCluster) {
				gridComputer.execute(() -> simpleJob.execute());
			} else {
				simpleJob.execute();
			}
		}
		if (distributedJob != null) {
			final List<Serializable> items = distributedJob.slice();
			if (items != null) {
				if (runInCluster) {
					items.stream().forEach(e -> gridComputer.execute(() -> distributedJob.execute(e)));
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

	public void setGridComputer(GridComputer gridComputer) {
		this.gridComputer = gridComputer;
	}

	public void setDistributedJob(DistributedJob<Serializable> distributedJob) {
		this.distributedJob = distributedJob;
	}

}
