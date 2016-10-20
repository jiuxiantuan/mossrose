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

import com.google.common.base.Throwables;
import com.jiuxian.theone.util.NetworkUtils;

public class ZookeeperClusterDiscovery implements ClusterDiscovery, Closeable {

	private String root;

	private CuratorFramework client;

	private int lockInterval = 3000;

	private static final String LOCK_NODE = "lock";
	private static final String DATA_NODE = "data";

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperClusterDiscovery.class);

	public ZookeeperClusterDiscovery(String root, String zks) {
		super();
		this.root = root;
		client = CuratorFrameworkFactory.newClient(zks, new ExponentialBackoffRetry(1000, 3));
		client.start();
		
		String dataPath = ZKPaths.makePath(root, DATA_NODE);
		try {
			if (client.checkExists().forPath(dataPath) == null) {
				LOGGER.info("Root data path {} not exists, create it.", dataPath);
				client.create().creatingParentsIfNeeded().forPath(dataPath);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}

	}

	@Override
	public List<String> findHosts() {
		String lockPath = ZKPaths.makePath(root, LOCK_NODE);
		String dataPath = ZKPaths.makePath(root, DATA_NODE);
		while (true) {
			try {
				// lock for create data
				client.create().withMode(CreateMode.EPHEMERAL).forPath(lockPath);
				// write data if lock sucess
				try {
					client.create().withMode(CreateMode.EPHEMERAL).forPath(ZKPaths.makePath(dataPath, NetworkUtils.getLocalIp()));
					break;
				} catch (NodeExistsException e) {
					throw Throwables.propagate(e);
				}
			} catch (Exception e1) {
				// Just ignore
			} finally {
				// release lock
				try {
					client.delete().forPath(lockPath);
				} catch (Exception e) {
					// Just ignore
				}
			}
			try {
				Thread.sleep(lockInterval);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		List<String> children = null;
		try {
			children = client.getChildren().forPath(dataPath);
		} catch (Exception e1) {
			throw Throwables.propagate(e1);
		}
		if (children != null && children.size() > 0) {
			String prefixToRemove = dataPath + "/";
			return children.stream().map(e -> StringUtils.removeStart(e, prefixToRemove)).collect(Collectors.toList());
		}
		throw new RuntimeException("No hosts get.");
	}

	@Override
	public void close() throws IOException {
		String dataPath = ZKPaths.makePath(root, DATA_NODE);
		try {
			client.delete().forPath(ZKPaths.makePath(dataPath, NetworkUtils.getLocalIp()));
		} catch (Exception e) {
			// ignore
		}
		if (client != null) {
			client.close();
		}
	}

}
