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
import com.jiuxian.mossrose.MossroseJob;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.SimpleJob;
import com.jiuxian.theone.Process;

public class QuartzProcess extends QuartzJobOperation implements Process, JobOperation {

	private Scheduler scheduler;

	private GridComputer gridComputer;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzProcess.class);

	private MossroseConfig mossroseConfig;

	public QuartzProcess(MossroseConfig mossroseConfig, GridComputer gridComputer) {
		super();
		this.mossroseConfig = Preconditions.checkNotNull(mossroseConfig);
		this.gridComputer = Preconditions.checkNotNull(gridComputer);
	}

	@Override
	public void run() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			// define the jobs
			List<JobMeta> jobs = mossroseConfig.getJobs();
			for (JobMeta jobMeta : jobs) {
				String mainClass = jobMeta.getMain();
				String id = jobMeta.getId() != null ? jobMeta.getId() : UUID.randomUUID().toString();
				String group = jobMeta.getGroup() != null ? jobMeta.getGroup() : "default-group";
				try {
					Class<?> jobClazz = Class.forName(mainClass);

					JobDetail job = JobBuilder.newJob(MossroseJob.class).withIdentity(id + "#job", group).build();
					try {
						Object jobInstance = jobClazz.newInstance();
						if (jobInstance instanceof SimpleJob) {
							job.getJobDataMap().put(JobDataMapKeys.SIMPLE_JOB, jobInstance);
						} else if (jobInstance instanceof DistributedJob) {
							job.getJobDataMap().put(JobDataMapKeys.DISTRIBUTED_JOB, jobInstance);
						} else {
							throw new RuntimeException("Invalid job instance, must be a " + SimpleJob.class + " or a " + DistributedJob.class);
						}
						job.getJobDataMap().put(JobDataMapKeys.GRID_COMPUTER, gridComputer);
						job.getJobDataMap().put(JobDataMapKeys.RUN_IN_CLUSTER, jobMeta.isRunInCluster());
					} catch (InstantiationException | IllegalAccessException e) {
						throw Throwables.propagate(e);
					}

					Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id + "trigger", group).startNow()
							.withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getCron())).build();

					// Tell quartz to schedule the job using our trigger
					scheduler.scheduleJob(job, trigger);
				} catch (ClassNotFoundException e) {
					throw Throwables.propagate(e);
				}
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
		if (gridComputer != null) {
			try {
				gridComputer.close();
			} catch (Exception e) {
			}
		}
	}

}
