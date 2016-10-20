package com.jiuxian.mossrose.cluster;

import java.util.List;

/**
 * Cluster discovery for work processes
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public interface ClusterDiscovery {

	List<String> findHosts();

}
