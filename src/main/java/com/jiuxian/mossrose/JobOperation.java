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

	void pauseAllJob();

	void resumeAllJob();

	JobRuntimeInfo jobInfo(String group, String id);

	void pauseJob(String group, String id);

	void resumeJob(String group, String id);

	void runJobNow(String group, String id);

	class JobRuntimeInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		public enum State {
			NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED
		}

		private String id;
		private String group;
		private String description;
		private String cron;
		private Date startTime;
		private Date endTime;
		private Date nextFireTime;
		private Date previousFireTime;

		private State state;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
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

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}

		@Override
		public String toString() {
			return "JobRuntimeInfo{" +
					"id='" + id + '\'' +
					", group='" + group + '\'' +
					", description='" + description + '\'' +
					", cron='" + cron + '\'' +
					", startTime=" + startTime +
					", endTime=" + endTime +
					", nextFireTime=" + nextFireTime +
					", previousFireTime=" + previousFireTime +
					", state=" + state +
					'}';
		}
	}

}
