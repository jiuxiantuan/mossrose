package com.jiuxian.mossrose;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Interface to manipulate jobs
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public interface JobOperation {

	List<JobRuntimeInfo> allJobs();

	public static class JobRuntimeInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private String id;
		private String cron;
		private boolean runInCluster;
		private String mainClass;
		private Date startTime;
		private Date endTime;
		private Date nextFireTime;
		private Date previousFireTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public boolean isRunInCluster() {
			return runInCluster;
		}

		public void setRunInCluster(boolean runInCluster) {
			this.runInCluster = runInCluster;
		}

		public String getMainClass() {
			return mainClass;
		}

		public void setMainClass(String mainClass) {
			this.mainClass = mainClass;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public Date getNextFireTime() {
			return nextFireTime;
		}

		public void setNextFireTime(Date nextFireTime) {
			this.nextFireTime = nextFireTime;
		}

		public Date getPreviousFireTime() {
			return previousFireTime;
		}

		public void setPreviousFireTime(Date previousFireTime) {
			this.previousFireTime = previousFireTime;
		}

		@Override
		public String toString() {
			return "JobRuntimeInfo [id=" + id + ", cron=" + cron + ", runInCluster=" + runInCluster + ", mainClass=" + mainClass + ", startTime="
					+ startTime + ", endTime=" + endTime + ", nextFireTime=" + nextFireTime + ", previousFireTime=" + previousFireTime + "]";
		}

	}

}
