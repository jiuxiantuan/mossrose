package com.jiuxian.mossrose.cluster;

import java.util.List;

public interface ClusterDiscovery {

	List<String> findHosts();
	
	String getClusterName();

}
