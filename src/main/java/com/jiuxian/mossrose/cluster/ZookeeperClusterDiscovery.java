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
package com.jiuxian.mossrose.cluster;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.jiuxian.theone.util.NetworkUtils;

public class ZookeeperClusterDiscovery implements ClusterDiscovery, Closeable {

	private String group;

	private CuratorFramework client;

	private static final String ZK_ROOT = "/mossrose/discovery";

	private static final int DEFAULT_RETRY_INTERVAL = 3000;
	private static final int DEFAULT_RETRIES = 10;

	private static final int DEFAULT_SESSION_TIMEOUT_MS = 10;
	private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 10;

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperClusterDiscovery.class);

	/**
	 * If the current host has been registered, interval in millseconds of retry
	 */
	private int retryInterval = DEFAULT_RETRY_INTERVAL;

	/**
	 * retry times, no retry with 0 or negative number
	 */
	private int retries = DEFAULT_RETRIES;

	public ZookeeperClusterDiscovery(String group, String zks) {
		this(group, zks, DEFAULT_SESSION_TIMEOUT_MS, DEFAULT_CONNECTION_TIMEOUT_MS);
	}

	/**
	 * @param group
	 *            cluster group to discovery with
	 * @param zks
	 *            zookeeper address
	 */
	public ZookeeperClusterDiscovery(String group, String zks, int sessionTimeoutMs, int connectionTimeoutMs) {
		super();
		this.group = group;
		client = CuratorFrameworkFactory.newClient(zks, 10000, 10000, new ExponentialBackoffRetry(1000, 3));
		client.start();

		try {
			// Create root
			final String groupPath = ZKPaths.makePath(ZK_ROOT, group);
			if (client.checkExists().forPath(groupPath) == null) {
				LOGGER.info("Group path {} not exists, create it.", groupPath);
				client.create().creatingParentsIfNeeded().forPath(groupPath);
			}

			// Register the current host
			final String host = NetworkUtils.getLocalIp();
			final String hostNode = ZKPaths.makePath(groupPath, host);

			// retry with conflict
			int retry = 0;
			boolean conflicted = true;
			while (retry++ <= retries) {
				conflicted = client.checkExists().forPath(hostNode) != null;
				if (conflicted) {
					LOGGER.info("Host {} already been registered on group path {}, wait for the {}th retry.", host, groupPath, retry);
					try {
						Thread.sleep(retryInterval);
					} catch (InterruptedException e) {
						throw Throwables.propagate(e);
					}
				}
			}
			if (conflicted) {
				throw new RuntimeException(
						"Host " + host + " has already been registered on group path " + groupPath + " ,  host should be unique in a group.");
			}

			LOGGER.info("Register host {} on group path {}.", host, groupPath);
			client.create().withMode(CreateMode.EPHEMERAL).forPath(hostNode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}

	}

	@Override
	public List<String> findHosts() {
		final String groupPath = ZKPaths.makePath(ZK_ROOT, group);
		List<String> children = null;
		try {
			children = client.getChildren().forPath(groupPath);
		} catch (Exception e1) {
			throw Throwables.propagate(e1);
		}
		if (children != null && children.size() > 0) {
			String prefixToRemove = groupPath + "/";
			return children.stream().map(e -> StringUtils.removeStart(e, prefixToRemove)).collect(Collectors.toList());
		}
		throw new RuntimeException("No hosts get.");
	}

	@Override
	public void close() throws IOException {
		final String groupPath = ZKPaths.makePath(ZK_ROOT, group);
		try {
			client.delete().forPath(ZKPaths.makePath(groupPath, NetworkUtils.getLocalIp()));
		} catch (Exception e) {
			// ignore
		}
		if (client != null) {
			client.close();
		}
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

}
