package com.jiuxian.mossrose;

import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.theone.zk.ZookeeperUniqueProcess;

/**
 * Mossrose basic implementation
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseProcess extends ZookeeperUniqueProcess {

	/**
	 * @param mossroseConfig
	 *            mossrose configuration
	 * @param clusterDiscovery
	 *            cluster discovery for workers
	 * @param zks
	 *            zookeeper address
	 */
	public MossroseProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery, String zks) {
		super(new QuartzProcess(mossroseConfig, clusterDiscovery), zks);
	}

	/**
	 * @param mossroseConfig
	 *            mossrose configuration
	 * @param clusterDiscovery
	 *            cluster discovery for workers
	 * @param zks
	 *            zookeeper address
	 * @param zkroot
	 *            Zookeeper root for the lock
	 * @param heartbeat
	 *            zookeeper heartbeat interval
	 * @param interval
	 *            interval for lock competition
	 */
	public MossroseProcess(MossroseConfig mossroseConfig, ClusterDiscovery clusterDiscovery, String zks, String zkroot, int heartbeat, int interval) {
		super(new QuartzProcess(mossroseConfig, clusterDiscovery), zks, zkroot, heartbeat, interval);
	}

}
