package com.jiuxian.mossrose;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
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
import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.SimpleJob;
import com.jiuxian.theone.Process;

public class MasterProcess implements Process, Closeable {

	private Scheduler scheduler;

	private Ignite ignite;

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcess.class);

	private MossroseConfig mossroseConfig;

	public MasterProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery) {
		super();
		this.mossroseConfig = Preconditions.checkNotNull(mossroseConfig);

		String clusterName = mossroseConfig.getCluster().getName();
		List<String> hosts = clusterDiscovery.findHosts();

		// Get ignite instance
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setGridName(clusterName);
		cfg.setMetricsLogFrequency(0);
		cfg.setGridLogger(new Slf4jLogger());
		TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
		ipFinder.setAddresses(hosts);
		discoSpi.setIpFinder(ipFinder);
		cfg.setDiscoverySpi(discoSpi);
		ignite = Ignition.start(cfg);

		LOGGER.info("Inital ignite cluser {} with hosts {}", clusterName, hosts);
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

					JobDetail job = JobBuilder.newJob(MossroseJob.class).withIdentity(id + "job", group).build();
					try {
						Object jobInstance = jobClazz.newInstance();
						if (jobInstance instanceof SimpleJob) {
							job.getJobDataMap().put("simpleJob", jobInstance);
						} else if (jobInstance instanceof DistributedJob) {
							job.getJobDataMap().put("distributedJob", jobInstance);
						} else {
							throw new RuntimeException("Invalid job instance, must be a " + SimpleJob.class + " or a " + DistributedJob.class);
						}
						job.getJobDataMap().put("ignite", ignite);
						job.getJobDataMap().put("runInCluster", jobMeta.isRunInCluster());
					} catch (InstantiationException | IllegalAccessException e) {
						throw Throwables.propagate(e);
					}

					Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id + "trigger", group).startNow().withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getCron())).build();

					// Tell quartz to schedule the job using our trigger
					scheduler.scheduleJob(job, trigger);
				} catch (ClassNotFoundException e) {
					throw Throwables.propagate(e);
				}
			}

			scheduler.start();
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
				LOGGER.error(e.getMessage(), e);
				// Just ignore
			}
		}
		if (ignite != null) {
			ignite.close();
		}
	}

}
