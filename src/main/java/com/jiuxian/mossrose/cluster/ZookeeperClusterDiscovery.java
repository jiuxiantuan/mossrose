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
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.jiuxian.theone.util.NetworkUtils;

public class ZookeeperClusterDiscovery implements ClusterDiscovery, Closeable {

	private String group;

	private CuratorFramework client;

	private static final String ZK_ROOT = "/mossrose/discovery";

	private static final String LOCK = "/mossrose/discovery-lock";

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
		Preconditions.checkNotNull(group);
		Preconditions.checkArgument(!Objects.equal(LOCK, group), "lock is retained, cannot be used as group name.");

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
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}

	}

	@Override
	public List<ClusterAddress> findHosts() {

		final String groupPath = ZKPaths.makePath(ZK_ROOT, group);
		List<String> children = null;
		try {
			children = client.getChildren().forPath(groupPath);
		} catch (Exception e1) {
			throw Throwables.propagate(e1);
		}
		if (children != null && children.size() > 0) {
			String prefixToRemove = groupPath + "/";
			return children.stream().map(e -> StringUtils.removeStart(e, prefixToRemove)).map(e -> new ClusterAddress(e))
					.collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public void registerCurrentAddress(ClusterAddress currentAddress) {
		try {
			// Register the current address
			final String address = currentAddress.toPlainAddress();
			final String groupPath = ZKPaths.makePath(ZK_ROOT, group);
			final String addressNode = ZKPaths.makePath(groupPath, address);

			// retry with conflict
			int retry = 0;
			boolean conflicted = true;
			while (retry++ <= retries) {
				conflicted = client.checkExists().forPath(addressNode) != null;
				if (conflicted) {
					LOGGER.info("Address {} already been registered on group path {}, wait for the {}th retry.", address, groupPath, retry);
					try {
						Thread.sleep(retryInterval);
					} catch (InterruptedException e) {
						throw Throwables.propagate(e);
					}
				}
			}
			if (conflicted) {
				throw new RuntimeException(
						"Address " + address + " has already been registered on group path " + groupPath + " ,  host should be unique in a group.");
			}

			LOGGER.info("Register host {} on group path {}.", address, groupPath);
			client.create().withMode(CreateMode.EPHEMERAL).forPath(addressNode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}
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

	@Override
	public boolean lock() {
		final String lockPath = ZKPaths.makePath(LOCK, group);
		try {
			if (client.checkExists().forPath(lockPath) == null) {
				try {
					client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(lockPath, NetworkUtils.getLocalIp().getBytes());
					return true;
				} catch (NodeExistsException e) {
					// Ignore as lock failed
				}

			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean unlock() {
		final String lockPath = ZKPaths.makePath(LOCK, group);
		try {
			client.delete().forPath(lockPath);
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}
	}

}
