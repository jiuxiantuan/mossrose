package com.jiuxian.mossrose.job.handler;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;

public abstract class AbstractJobHandler implements JobHandler {

    protected ClusterGroup select(Ignite ignite) {
//        final ClusterGroup clusterGroup = ignite.cluster().forAttribute(IgniteConsts.STATE, IgniteConsts.STATE_READY);
//        if (clusterGroup.nodes().size() > 1) {
//            return clusterGroup.forPredicate(p -> !p.<Boolean>attribute(IgniteConsts.ONLY_TRIGGER));
//        }
//        return clusterGroup;

        return ignite.cluster().forServers();
    }
}
