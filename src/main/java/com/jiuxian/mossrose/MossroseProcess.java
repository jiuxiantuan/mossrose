package com.jiuxian.mossrose;

import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.theone.zk.ZookeeperGuardProcess;

public class MossroseProcess extends ZookeeperGuardProcess {

	public MossroseProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery, String zks) {
		super(new QuartzProcess(mossroseConfig, clusterDiscovery), zks);
	}

	public MossroseProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery, String zks, String zkroot, int heartbeat, int interval) {
		super(new QuartzProcess(mossroseConfig, clusterDiscovery), zks, zkroot, heartbeat, interval);
	}

}
