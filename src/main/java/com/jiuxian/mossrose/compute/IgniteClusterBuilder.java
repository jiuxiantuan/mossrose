/**
 * Copyright 2015-2020 jiuxian.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose.compute;

import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster.LoadBalancingMode;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ExecutorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.apache.ignite.spi.loadbalancing.roundrobin.RoundRobinLoadBalancingSpi;
import org.apache.ignite.spi.loadbalancing.weightedrandom.WeightedRandomLoadBalancingSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public final class IgniteClusterBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(IgniteClusterBuilder.class);

    public static Ignite build(final MossroseConfig mossroseConfig) {
        final Cluster cluster = mossroseConfig.getCluster();

        // Get ignite instance
        final String clusterName = cluster.getName();
        final LoadBalancingMode loadBalancingMode = cluster.getLoadBalancingMode();

        // constuct ignite
        final TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
        try {
            discoSpi.setLocalAddress(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            LOGGER.warn("Error while getting local address.", e);
        }
        discoSpi.setLocalPort(cluster.getPort());

        final TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
        ipFinder.setZkConnectionString(cluster.getDiscoveryZk());
        ipFinder.setServiceName(cluster.getName());
        ipFinder.setBasePath("/mossrose-discovery");
        discoSpi.setIpFinder(ipFinder);

        final IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName(clusterName);
        cfg.setMetricsLogFrequency(0);
        cfg.setGridLogger(new Slf4jLogger());
        cfg.setDiscoverySpi(discoSpi);

        CacheConfiguration cacheCfg = new CacheConfiguration();
        cacheCfg.setName(clusterName);
        cacheCfg.setBackups(0);
        cfg.setCacheConfiguration(cacheCfg);

        if (loadBalancingMode == LoadBalancingMode.ROUND_ROBIN) {
            cfg.setLoadBalancingSpi(new RoundRobinLoadBalancingSpi());
        } else if (loadBalancingMode == LoadBalancingMode.RANDOM) {
            cfg.setLoadBalancingSpi(new WeightedRandomLoadBalancingSpi());
        }

        // Isolate thread pool for jobs
        final List<MossroseConfig.JobMeta> jobs = mossroseConfig.getJobs();
        if (jobs != null) {
            final ExecutorConfiguration[] executorConfigurations = jobs.stream()
                    .map(job -> new ExecutorConfiguration(job.getId()).setSize(job.getThreads()))
                    .toArray(ExecutorConfiguration[]::new);

            cfg.setExecutorConfiguration(executorConfigurations);
        }

        // SPI
        IgniteConfigurationRenderRegistry.render(cfg);

        final Ignite ignite = Ignition.start(cfg);
        LOGGER.info("{} join ignite cluser", ignite.cluster().localNode().addresses());

        return ignite;
    }

}
