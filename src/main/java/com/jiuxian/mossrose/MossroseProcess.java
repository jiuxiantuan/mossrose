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

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.IgniteGridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.quartz.QuartzProcess;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(MossroseProcess.class);

    /**
     * @param mossroseConfig
     *            mossrose configuration
     */
    public MossroseProcess(final MossroseConfig mossroseConfig) {
        mossroseConfig.applyDefault();

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
        });

    }

    public JobOperation getJobOperation() {
        return quartzProcess;
    }

    public LeaderSelector getLeaderSelector() {
        return leaderSelector;
    }

    public void run() {
        gridComputer.init();

        leaderSelector.start();
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
