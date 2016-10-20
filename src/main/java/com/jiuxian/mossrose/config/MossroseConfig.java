package com.jiuxian.mossrose.config;

import java.util.List;

import com.google.common.base.Preconditions;

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

		/**
		 * cluster name
		 */
		private String name;

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Cluster [name=" + name + "]";
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
		 * If true, the job will run on any node in the cluster; If false，the
		 * job will run on master node
		 */
		private boolean runInCluster;

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

		public boolean isRunInCluster() {
			return runInCluster;
		}

		@Override
		public String toString() {
			return "JobMeta [id=" + id + ", cron=" + cron + ", main=" + main + ", group=" + group + "]";
		}

	}

	public Cluster getCluster() {
		return cluster;
	}

	public List<JobMeta> getJobs() {
		return jobs;
	}

	@Override
	public String toString() {
		return "MossroseConfig [cluster=" + cluster + ", jobs=" + jobs + "]";
	}

	public void validate() {
		Preconditions.checkNotNull(cluster);
		Preconditions.checkNotNull(cluster.getName());

		Preconditions.checkNotNull(jobs);
		Preconditions.checkArgument(jobs.size() > 0);
		for (JobMeta meta : jobs) {
			Preconditions.checkNotNull(meta.getCron());
			Preconditions.checkNotNull(meta.getMain());
		}
	}

}
