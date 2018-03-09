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
package com.jiuxian.mossrose.config;

import java.util.List;

/**
 * Mossrose meta configuration
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseConfig {

	private Cluster cluster;
	private List<JobMeta> jobs;

	/**
	 * cluster meta
	 * 
	 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
	 *
	 */
	public static class Cluster {

		private static final int DEFAULT_PORT = 18888;

		/**
		 * cluster name
		 */
		private String name;

		private int port = DEFAULT_PORT;

		private LoadBalancingMode loadBalancingMode;

		/**
		 * 用于节点发现的zk
		 */
		private String discoveryZk;

		/**
		 * 是否在master节点上运行任务
		 */
		private boolean runOnMaster = true;

		public boolean isRunOnMaster() {
			return runOnMaster;
		}

		public void setRunOnMaster(boolean runOnMaster) {
			this.runOnMaster = runOnMaster;
		}

		public String getDiscoveryZk() {
			return discoveryZk;
		}

		public void setDiscoveryZk(String discoveryZk) {
			this.discoveryZk = discoveryZk;
		}

		public String getName() {
			return name;
		}

		public LoadBalancingMode getLoadBalancingMode() {
			return loadBalancingMode;
		}

		public int getPort() {
			return port;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public void setLoadBalancingMode(LoadBalancingMode loadBalancingMode) {
			this.loadBalancingMode = loadBalancingMode;
		}

		@Override
		public String toString() {
			return "Cluster [name=" + name + ", port=" + port + ", loadBalancingMode=" + loadBalancingMode + "]";
		}

		public enum LoadBalancingMode {
			ROUND_ROBIN, RANDOM
		}

	}

	/**
	 * job meta
	 * 
	 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
	 *
	 */
	public static class JobMeta {
		/**
		 * job id
		 */
		private String id;
		/**
		 * cron
		 */
		private String cron;
		/**
		 * class name of job
		 */
		private String main;
		/**
		 * group
		 */
		private String group;
		/**
		 * description
		 */
		private String description;
		
		/**
		 * Job bean name in spring
		 */
		private String jobBeanName;

		public String getJobBeanName() {
			return jobBeanName;
		}

		public void setJobBeanName(String jobBeanName) {
			this.jobBeanName = jobBeanName;
		}

		public String getId() {
			return id;
		}

		public String getCron() {
			return cron;
		}

		public String getMain() {
			return main;
		}

		public String getGroup() {
			return group;
		}

		public String getDescription() {
			return description;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setMain(String main) {
			this.main = main;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return "JobMeta{" +
					"id='" + id + '\'' +
					", cron='" + cron + '\'' +
					", main='" + main + '\'' +
					", group='" + group + '\'' +
					", description='" + description + '\'' +
					", jobBeanName='" + jobBeanName + '\'' +
					'}';
		}
	}

	public Cluster getCluster() {
		return cluster;
	}

	public List<JobMeta> getJobs() {
		return jobs;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public void setJobs(List<JobMeta> jobs) {
		this.jobs = jobs;
	}

	@Override
	public String toString() {
		return "MossroseConfig [cluster=" + cluster + ", jobs=" + jobs + "]";
	}

}
