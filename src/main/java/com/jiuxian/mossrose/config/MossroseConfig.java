package com.jiuxian.mossrose.config;

import java.util.List;

import com.google.common.base.Preconditions;

public class MossroseConfig {

	private Cluster cluster;
	private List<JobMeta> jobs;

	public static class Cluster {

		private String name;

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Cluster [name=" + name + "]";
		}

	}

	public static class JobMeta {
		private String id;
		private String cron;
		private String main;
		private String group;
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
