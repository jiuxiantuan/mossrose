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

import com.google.common.collect.Iterables;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.JobOperation.JobRuntimeInfo.State;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuartzJobOperation implements JobOperation {

	private Scheduler scheduler;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobOperation.class);

	protected void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public List<JobRuntimeInfo> allJobs() {
		return ifSchedulerAvaliable(() -> {
			final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
			if (jobKeys != null) {
				return jobKeys.stream().map(e -> getJobInfoByKey(e)).collect(Collectors.toList());
			}
			return null;
		});
	}

	@Override
	public void pauseJob(String group, String id) {
		ifSchedulerAvaliable(() -> scheduler.pauseJob(new JobKey(id, group)));
	}

	@Override
	public void resumeJob(String group, String id) {
		ifSchedulerAvaliable(() -> scheduler.resumeJob(new JobKey(id, group)));
	}

	@Override
	public void runJobNow(String group, String id) {
		ifSchedulerAvaliable(() -> scheduler.triggerJob(new JobKey(id, group)));
	}

	@Override
	public void pauseAllJob() {
		ifSchedulerAvaliable(scheduler::pauseAll);
	}

	@Override
	public void resumeAllJob() {
		ifSchedulerAvaliable(scheduler::resumeAll);
	}

	@Override
	public JobRuntimeInfo jobInfo(String group, String id) {
		return ifSchedulerAvaliable(() -> getJobInfoByKey(new JobKey(id, group)));
	}

	private JobRuntimeInfo getJobInfoByKey(final JobKey jobKey) {
		JobRuntimeInfo job = null;
		try {
			final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			// Only one trigger for a job
			final Trigger trigger = Iterables.getFirst(scheduler.getTriggersOfJob(jobKey), null);

			if (trigger != null) {
				job = new JobRuntimeInfo();

				job.setId(jobKey.getName());
				job.setGroup(jobKey.getGroup());
				job.setDescription(jobDetail.getDescription());

				final JobDataMap dataMap = jobDetail.getJobDataMap();

				final Object jobMain = dataMap.get(JobDataMapKeys.MJOB);
				if (jobMain != null) {
					job.setMainClass(jobMain.getClass().getName());
				}

				job.setStartTime(trigger.getStartTime());
				job.setEndTime(trigger.getEndTime());
				job.setPreviousFireTime(trigger.getPreviousFireTime());
				job.setNextFireTime(trigger.getNextFireTime());
				job.setState(State.valueOf(scheduler.getTriggerState(trigger.getKey()).name()));
				if (trigger instanceof CronTrigger) {
					job.setCron(((CronTrigger) trigger).getCronExpression());
				}
			}
		} catch (SchedulerException eInGettingJobInfo) {
			LOGGER.error(eInGettingJobInfo.getMessage(), eInGettingJobInfo);
		}
		return job;
	}

	private void ifSchedulerAvaliable(QuartzOp op) {
		try {
			if (isSchedulerAvaliable()) {
				op.apply();
			}
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private <T> T ifSchedulerAvaliable(ResultQuartzOp<T> op) {
		try {
			if (isSchedulerAvaliable()) {
				return op.apply();
			}
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	private boolean isSchedulerAvaliable() throws SchedulerException {
		return scheduler != null && scheduler.isStarted();
	}

	@FunctionalInterface
	public interface QuartzOp {
		void apply() throws SchedulerException;
	}

	@FunctionalInterface
	public interface ResultQuartzOp<T> {
		T apply() throws SchedulerException;
	}
}
