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
package com.jiuxian.mossrose.compute;

import java.util.List;
import java.util.stream.Collectors;

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
import com.jiuxian.mossrose.cluster.ClusterAddress;
import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster.LoadBalancingMode;
import com.jiuxian.theone.util.NetworkUtils;

public final class IgniteClusterBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(IgniteClusterBuilder.class);

	public static Ignite build(Cluster cluster, ClusterDiscovery clusterDiscovery) {
		// Get ignite instance
		final String clusterName = cluster.getName();
		final LoadBalancingMode loadBalancingMode = Objects.firstNonNull(cluster.getLoadBalancingMode(), LoadBalancingMode.ROUND_ROBIN);

		// find ignite cluster
		final ClusterAddress currentAddress = new ClusterAddress(NetworkUtils.getLocalIp(), cluster.getPort());
		final List<ClusterAddress> hosts = clusterDiscovery.findHosts(currentAddress);

		// constuct ignite
		final TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
		ipFinder.setAddresses(hosts.stream().map(e -> e.toPlainAddress()).collect(Collectors.toList()));

		final TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
		discoSpi.setIpFinder(ipFinder);
		discoSpi.setLocalPort(cluster.getPort());

		final IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setGridName(clusterName);
		cfg.setMetricsLogFrequency(0);
		cfg.setGridLogger(new Slf4jLogger());
		cfg.setDiscoverySpi(discoSpi);

		if (loadBalancingMode == LoadBalancingMode.ROUND_ROBIN) {
			cfg.setLoadBalancingSpi(new RoundRobinLoadBalancingSpi());
		} else if (loadBalancingMode == LoadBalancingMode.RANDOM) {
			cfg.setLoadBalancingSpi(new WeightedRandomLoadBalancingSpi());
		}

		final Ignite ignite = Ignition.start(cfg);
		ignite.compute().broadcast(new IgniteRunnable() {

			private static final long serialVersionUID = 1L;

			@Override
			public void run() {
				System.out.println("Join ignite cluser " + clusterName + " with hosts " + hosts);
			}
		});
		LOGGER.info("Inital ignite cluser {} with hosts {}", clusterName, hosts);

		return ignite;
	}

}