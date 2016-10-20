package com.jiuxian.mossrose.compute;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.loadbalancing.roundrobin.RoundRobinLoadBalancingSpi;
import org.apache.ignite.spi.loadbalancing.weightedrandom.WeightedRandomLoadBalancingSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster.LoadBalancingMode;

public class IgniteGridComputer implements GridComputer {

	private final Ignite ignite;

	private static final Logger LOGGER = LoggerFactory.getLogger(IgniteGridComputer.class);

	public IgniteGridComputer(Cluster cluster, ClusterDiscovery clusterDiscovery) {
		String clusterName = cluster.getName();
		LoadBalancingMode loadBalancingMode = Objects.firstNonNull(cluster.getLoadBalancingMode(), LoadBalancingMode.ROUND_ROBIN);
		List<String> hosts = clusterDiscovery.findHosts();

		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setGridName(clusterName);
		cfg.setMetricsLogFrequency(0);
		cfg.setGridLogger(new Slf4jLogger());
		TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
		ipFinder.setAddresses(hosts);
		discoSpi.setIpFinder(ipFinder);
		cfg.setDiscoverySpi(discoSpi);

		if (loadBalancingMode == LoadBalancingMode.ROUND_ROBIN) {
			RoundRobinLoadBalancingSpi roundRobinLoadBalancingSpi = new RoundRobinLoadBalancingSpi();
			roundRobinLoadBalancingSpi.setPerTask(true);
			cfg.setLoadBalancingSpi(roundRobinLoadBalancingSpi);
		} else if (loadBalancingMode == LoadBalancingMode.RANDOM) {
			cfg.setLoadBalancingSpi(new WeightedRandomLoadBalancingSpi());
		}

		ignite = Ignition.start(cfg);
		ignite.compute().broadcast(new IgniteRunnable() {

			private static final long serialVersionUID = 1L;

			@Override
			public void run() {
				System.out.println("Join ignite cluser " + clusterName + "with hosts " + hosts);
			}
		});
		LOGGER.info("Inital ignite cluser {} with hosts {}", clusterName, hosts);
	}

	@Override
	public void execute(GridCompute gridCompute) {
		ignite.compute().run(() -> gridCompute.apply());
	}

	@Override
	public void close() throws Exception {
		if (ignite != null) {
			ignite.close();
		}
	}

}
