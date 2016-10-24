package com.jiuxian.mossrose.quartz;

import java.util.List;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.JobOperation.JobRuntimeInfo.State;

public class QuartzJobOperation implements JobOperation {

	private Scheduler scheduler;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobOperation.class);

	protected void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public List<JobRuntimeInfo> allJobs() {
		if (isSchedulerAvaliable()) {
			final List<JobRuntimeInfo> jobs = Lists.newArrayList();
			try {
				Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
				if (jobKeys != null) {
					jobKeys.stream().forEach(jobKey -> {
						JobRuntimeInfo job = getInfoInfoByKey(jobKey);
						if (job != null) {
							jobs.add(job);
						}
					});
				}
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return jobs;
		}
		return null;
	}

	@Override
	public void pauseJob(String group, String id) {
		if (isSchedulerAvaliable()) {
			try {
				scheduler.pauseJob(new JobKey(id, group));
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void resumeJob(String group, String id) {
		if (isSchedulerAvaliable()) {
			try {
				scheduler.resumeJob(new JobKey(id, group));
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void runJobNow(String group, String id) {
		if (isSchedulerAvaliable()) {
			try {
				scheduler.triggerJob(new JobKey(id, group));
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void pauseAllJob() {
		if (isSchedulerAvaliable()) {
			try {
				scheduler.pauseAll();
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void resumeAllJob() {
		if (isSchedulerAvaliable()) {
			try {
				scheduler.resumeAll();
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public JobRuntimeInfo jobInfo(String group, String id) {
		JobRuntimeInfo job = null;
		if (isSchedulerAvaliable()) {
			job = getInfoInfoByKey(new JobKey(id, group));
		}
		return job;
	}

	private JobRuntimeInfo getInfoInfoByKey(final JobKey jobKey) {
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

				JobDataMap dataMap = jobDetail.getJobDataMap();

				job.setRunInCluster(dataMap.getBoolean(JobDataMapKeys.RUN_IN_CLUSTER));
				Object jobMain = Objects.firstNonNull(dataMap.get(JobDataMapKeys.SIMPLE_JOB), dataMap.get(JobDataMapKeys.DISTRIBUTED_JOB));
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

	private boolean isSchedulerAvaliable() {
		try {
			return scheduler != null && scheduler.isStarted();
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
}
