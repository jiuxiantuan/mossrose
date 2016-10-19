package com.jiuxian.mossrose;

import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.theone.zk.ZookeeperGuardProcess;

/**
 * Mossrose主类
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseProcess extends ZookeeperGuardProcess {

	public MossroseProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery, String zks) {
		super(new QuartzProcess(mossroseConfig, clusterDiscovery), zks);
	}

	public MossroseProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery, String zks, String zkroot, int heartbeat, int interval) {
		super(new QuartzProcess(mossroseConfig, clusterDiscovery), zks, zkroot, heartbeat, interval);
	}

}
