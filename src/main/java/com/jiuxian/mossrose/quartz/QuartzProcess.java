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

import com.google.common.base.Preconditions;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import org.apache.ignite.Ignite;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class QuartzProcess extends QuartzJobOperation implements JobOperation, AutoCloseable {

	private Scheduler scheduler;

	private Ignite ignite;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzProcess.class);

	private MossroseConfig mossroseConfig;

	public QuartzProcess(MossroseConfig mossroseConfig) {
		super();
		this.mossroseConfig = Preconditions.checkNotNull(mossroseConfig);
	}

	public void setIgnite(Ignite ignite) {
		this.ignite = ignite;
	}

	public void run() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			// define the jobs
			final List<JobMeta> jobs = mossroseConfig.getJobs();
			for (final JobMeta jobMeta : jobs) {
				LOGGER.info("Load job: {}", jobMeta);

				final String id = jobMeta.getId();
				final String group = jobMeta.getGroup();
				final JobDetail job = JobBuilder.newJob(QuartzJobWrapper.class).withIdentity(id, group).withDescription(jobMeta.getDescription())
						.build();

				job.getJobDataMap().put(JobDataMapKeys.JOB_META, jobMeta);
				job.getJobDataMap().put(JobDataMapKeys.IGNITE, ignite);
				job.getJobDataMap().put(JobDataMapKeys.RUN_ON_MASTER, mossroseConfig.getCluster().isRunOnMaster());

				final Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id + "trigger", group).startNow()
						.withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getCron())).build();

				// Tell quartz to schedule the job using our trigger
				scheduler.scheduleJob(job, trigger);
			}
			super.setScheduler(scheduler);

			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
			}
		}
	}

}
