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

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.theone.Process;

public class QuartzProcess extends QuartzJobOperation implements Process, JobOperation {

	private Scheduler scheduler;

	private GridComputer gridComputer;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzProcess.class);

	private MossroseConfig mossroseConfig;

	public QuartzProcess(MossroseConfig mossroseConfig) {
		super();
		this.mossroseConfig = Preconditions.checkNotNull(mossroseConfig);
	}

	public void setGridComputer(GridComputer gridComputer) {
		this.gridComputer = gridComputer;
	}

	@Override
	public void run() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			// define the jobs
			List<JobMeta> jobs = mossroseConfig.getJobs();
			for (JobMeta jobMeta : jobs) {
				final String id = jobMeta.getId() != null ? jobMeta.getId() : UUID.randomUUID().toString();
				final String group = jobMeta.getGroup() != null ? jobMeta.getGroup() : "default-group";
				final JobDetail job = JobBuilder.newJob(QuartzJobWrapper.class).withIdentity(id, group).withDescription(jobMeta.getDescription())
						.build();

//				job.getJobDataMap().put(JobDataMapKeys.MJOB, createJobBean(jobMeta));
				job.getJobDataMap().put(JobDataMapKeys.GRID_COMPUTER, gridComputer);
				job.getJobDataMap().put("jobMeta", jobMeta);

				final Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id + "trigger", group).startNow()
						.withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getCron())).build();

				// Tell quartz to schedule the job using our trigger
				scheduler.scheduleJob(job, trigger);
			}

			scheduler.start();

			super.setScheduler(scheduler);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
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
