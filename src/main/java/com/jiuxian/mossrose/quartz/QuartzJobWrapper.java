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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MJob;
import com.jiuxian.mossrose.job.handler.MJobHandler;
import com.jiuxian.mossrose.job.handler.MJobHandlerFactory;
import com.jiuxian.mossrose.job.to.ClassnameObjectResource;
import com.jiuxian.mossrose.job.to.ObjectResource;
import com.jiuxian.mossrose.job.to.SpringBeanObjectResource;

public class QuartzJobWrapper implements Job {

	// private MJob mJob;

	private GridComputer gridComputer;

	private JobMeta jobMeta;

	private ObjectResource objectResource;

	// private String jobId;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobWrapper.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final Stopwatch watch = Stopwatch.createStarted();
		@SuppressWarnings("unchecked")
		final MJobHandler<MJob<Serializable>> mJobHandler = (MJobHandler<MJob<Serializable>>) MJobHandlerFactory.getInstance()
				.getMJobHandler(objectResource.clazz());
		mJobHandler.handle(jobMeta, objectResource, gridComputer);
		watch.stop();
		LOGGER.info("Job {} use time: {} ms.", jobMeta.getId(), watch.elapsed(TimeUnit.MILLISECONDS));
	}

	// public void setMJob(MJob mJob) {
	// this.mJob = mJob;
	// }

	public void setGridComputer(GridComputer gridComputer) {
		this.gridComputer = gridComputer;
	}
	//
	// public void setJobId(String jobId) {
	// this.jobId = jobId;
	// }

	public void setJobMeta(JobMeta jobMeta) {
		this.jobMeta = jobMeta;
		ObjectResource objectResource = null;
		if (!Strings.isNullOrEmpty(jobMeta.getMain())) {
			objectResource = new ClassnameObjectResource(jobMeta.getMain());
		} else if (!Strings.isNullOrEmpty(jobMeta.getJobBeanName())) {
			objectResource = new SpringBeanObjectResource(jobMeta.getJobBeanName());
		} else {
			throw new IllegalArgumentException("property 'main' and 'job-bean-name' must exists one.");
		}
		this.objectResource = objectResource;
	}

}
