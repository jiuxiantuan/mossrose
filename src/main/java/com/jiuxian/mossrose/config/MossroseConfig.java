package com.jiuxian.mossrose.config;

import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Mossrose配置，用于配置集群及任务元信息
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseConfig {

	private Cluster cluster;
	private List<JobMeta> jobs;

	public static class Cluster {

		/**
		 * 集群名字
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
	 * 任务元信息
	 * 
	 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
	 *
	 */
	public static class JobMeta {
		/**
		 * 任务id
		 */
		private String id;
		/**
		 * 任务cron表达式
		 */
		private String cron;
		/**
		 * 任务类全名
		 */
		private String main;
		/**
		 * 任务组
		 */
		private String group;
		/**
		 * 是否在集群中执行，如果为false，则直接在主节点上执行，如果为true，则会平均分配到集群中执行
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
