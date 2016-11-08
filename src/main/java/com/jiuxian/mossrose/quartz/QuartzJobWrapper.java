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
package com.jiuxian.mossrose.quartz;

import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.MJob;
import com.jiuxian.mossrose.job.factory.MJobHandler;
import com.jiuxian.mossrose.job.factory.MJobHandlerFactory;

public class QuartzJobWrapper implements Job {

	private MJob mJob;

	private GridComputer gridComputer;

	private String jobId;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobWrapper.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final Stopwatch watch = Stopwatch.createStarted();
		@SuppressWarnings("unchecked")
		final MJobHandler<MJob> mJobHandler = (MJobHandler<MJob>) MJobHandlerFactory.getInstance().getMJobHandler(mJob.getClass());
		mJobHandler.handle(mJob, gridComputer);
		watch.stop();
		LOGGER.info("Job {} use time: {} ms.", jobId, watch.elapsed(TimeUnit.MILLISECONDS));
	}

	public void setMJob(MJob mJob) {
		this.mJob = mJob;
	}

	public void setGridComputer(GridComputer gridComputer) {
		this.gridComputer = gridComputer;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

}
