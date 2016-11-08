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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.jiuxian.mossrose.compute.ComputeUnit;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.SimpleJob;

public class MossroseJob implements Job {

	private SimpleJob simpleJob;

	private DistributedJob<Serializable> distributedJob;

	private GridComputer gridComputer;

	private String jobId;

	private static final Logger LOGGER = LoggerFactory.getLogger(MossroseJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final Stopwatch watch = Stopwatch.createStarted();
		if (simpleJob != null) {
			gridComputer.execute(simpleJob::execute);
		}
		if (distributedJob != null) {
			final List<Serializable> items = distributedJob.slice();
			if (items != null) {
				gridComputer.execute(items.stream().<ComputeUnit> map(e -> () -> distributedJob.execute(e)).collect(Collectors.toList()));
			}
		}
		watch.stop();
		LOGGER.info("Job {} use time: {} ms.", jobId, watch.elapsed(TimeUnit.MILLISECONDS));
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

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

}
