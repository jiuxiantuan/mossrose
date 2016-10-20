/**
 * Copyright 2015-2020 jiuxian.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose;

import java.io.Serializable;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jiuxian.mossrose.compute.GridCompute;
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
				gridComputer.execute(new SimpleJobUnit(simpleJob));
			} else {
				simpleJob.execute();
			}
		}
		if (distributedJob != null) {
			final List<Serializable> items = distributedJob.slice();
			if (items != null) {
				if (runInCluster) {
					items.stream().forEach(e -> gridComputer.execute(new DistributeJobUnit(distributedJob, e)));
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

	public static class SimpleJobUnit implements GridCompute {
		private static final long serialVersionUID = 1L;

		private final SimpleJob simpleJob;

		public SimpleJobUnit(SimpleJob simpleJob) {
			super();
			this.simpleJob = simpleJob;
		}

		@Override
		public void apply() {
			simpleJob.execute();
		}
	}

	public static class DistributeJobUnit implements GridCompute {
		private static final long serialVersionUID = 1L;

		private final DistributedJob<Serializable> distributedJob;

		private final Serializable slice;

		public DistributeJobUnit(DistributedJob<Serializable> distributedJob, Serializable slice) {
			super();
			this.distributedJob = distributedJob;
			this.slice = slice;
		}

		@Override
		public void apply() {
			distributedJob.execute(slice);
		}
	}

}
