package com.jiuxian.mossrose.quartz;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
