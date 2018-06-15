package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.config.MossroseConfig;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;

public abstract class AbstractJobHandler implements JobHandler {

    private boolean runOnMaster;

    @Override
    public void handle(MossroseConfig.JobMeta jobMeta, Ignite ignite, boolean runOnMaster) {
        this.runOnMaster = runOnMaster;
        handle(jobMeta, ignite);
    }

    protected abstract void handle(MossroseConfig.JobMeta jobMeta, Ignite ignite);


    protected ClusterGroup select(Ignite ignite) {
        final ClusterGroup clusterGroup = ignite.cluster().forRemotes();
        if(runOnMaster || clusterGroup.hostNames().isEmpty()) {
            return ignite.cluster().forServers();
        }

        return clusterGroup;
    }
}
