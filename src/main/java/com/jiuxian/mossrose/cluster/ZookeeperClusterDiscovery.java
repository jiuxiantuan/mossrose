package com.jiuxian.mossrose.cluster;

import java.util.List;

import com.google.common.base.Preconditions;
import com.jiuxian.theone.Guard;

public class ZookeeperClusterDiscovery implements ClusterDiscovery {

	private String clusterName;

	private Guard guard;

	public ZookeeperClusterDiscovery(String clusterName, Guard guard) {
		super();
		this.clusterName = Preconditions.checkNotNull(clusterName);
		this.guard = Preconditions.checkNotNull(guard);

	}

	@Override
	public List<String> findHosts() {
		return guard.competers(clusterName);
	}

	@Override
	public String getClusterName() {
		return clusterName;
	}

}
