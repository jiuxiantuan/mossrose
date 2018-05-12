/**
 * Copyright 2015-2020 jiuxian.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose;

import com.google.common.base.Strings;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.IgniteGridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.job.to.ClassnameObjectResource;
import com.jiuxian.mossrose.job.to.ObjectContainer;
import com.jiuxian.mossrose.job.to.SpringBeanObjectResource;
import com.jiuxian.mossrose.quartz.QuartzProcess;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Mossrose basic implementation<br>
 *
 * Use zookeeper for master election, zookeeper for discovery, and ignite for
 * grid computation
 *
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseProcess implements AutoCloseable {

    private final QuartzProcess quartzProcess;
    private final GridComputer gridComputer;
    private final LeaderSelector leaderSelector;
    private final CuratorFramework client;

    private final MossroseConfig mossroseConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(MossroseProcess.class);

    /**
     * @param mossroseConfig
     *            mossrose configuration
     */
    public MossroseProcess(final MossroseConfig mossroseConfig) {
        mossroseConfig.applyDefault();
        this.mossroseConfig = mossroseConfig;

        this.quartzProcess = new QuartzProcess(mossroseConfig);
        this.gridComputer = new IgniteGridComputer(mossroseConfig);

        quartzProcess.setGridComputer(gridComputer);

        this.client = CuratorFrameworkFactory.newClient(mossroseConfig.getCluster().getDiscoveryZk(), new BoundedExponentialBackoffRetry(1000, 8000, 4));
        client.start();

        final String leaderPath = "/mossrose-lock/" + mossroseConfig.getCluster().getName();
        this.leaderSelector = new LeaderSelector(client, leaderPath, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                LOGGER.info("Become leader.");
                quartzProcess.run();

                // Block for leader
                synchronized (this) {
                    try {
                        while (true) {
                            this.wait();
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState){
                if ( client.getConnectionStateErrorPolicy().isErrorState(newState) ) {
                    try {
                        LOGGER.warn("Shutdown quartz after state: {}", newState);
                        quartzProcess.close();
                    } catch (IOException e) {
                        LOGGER.error("Error while shutting down quartz process.", e);
                    }
                }
                super.stateChanged(client, newState);
            }
        });

    }

    public JobOperation getJobOperation() {
        return quartzProcess;
    }

    public LeaderSelector getLeaderSelector() {
        return leaderSelector;
    }

    public void run() {
        initObjectContainer();

        gridComputer.init();

        leaderSelector.autoRequeue();
        leaderSelector.start();
    }

    private void initObjectContainer() {
        ObjectContainer.putGridComputer(gridComputer);
        final List<MossroseConfig.JobMeta> jobs = mossroseConfig.getJobs();
        if(jobs != null) {
            jobs.forEach(jobMeta -> {
                final String id = jobMeta.getId();
                Object job = null;
                if (!Strings.isNullOrEmpty(jobMeta.getMain())) {
                    job = new ClassnameObjectResource(jobMeta.getMain()).generate();
                } else if (!Strings.isNullOrEmpty(jobMeta.getJobBeanName())) {
                    job = new SpringBeanObjectResource(jobMeta.getJobBeanName()).generate();
                }
                ObjectContainer.put(id, job);
            });
        }

    }

    @Override
    public void close() throws Exception {
        if(client != null) {
            client.close();
        }
        if(leaderSelector != null) {
            leaderSelector.close();
        }
    }
}
