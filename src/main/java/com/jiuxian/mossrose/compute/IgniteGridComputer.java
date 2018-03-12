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
package com.jiuxian.mossrose.compute;

import com.jiuxian.mossrose.config.MossroseConfig;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.lang.IgniteFuture;

import java.io.Serializable;

/**
 * @author yuxuan.wang
 */
public class IgniteGridComputer implements GridComputer {

    public static class IgniteComputeFuture implements ComputeFuture {
        private final IgniteFuture<Object> igniteFuture;

        IgniteComputeFuture(final IgniteFuture<Object> igniteFuture) {
            super();
            this.igniteFuture = igniteFuture;
        }

        @Override
        public Serializable join() {
            return (Serializable) igniteFuture.get();
        }

    }

    private Ignite ignite;

    private final MossroseConfig mossroseConfig;

    public IgniteGridComputer(final MossroseConfig mossroseConfig) {
        this.mossroseConfig = mossroseConfig;
    }

    @Override
    public void init() {
        ignite = IgniteClusterBuilder.build(mossroseConfig);
    }

    @Override
    public ComputeFuture execute(final String taskId, final ComputeUnit gridCompute) {
        // get run info by run-on-master
        final ClusterGroup clusterGroup = ignite.cluster().forRemotes();
        IgniteCompute compute;
        if(mossroseConfig.getCluster().isRunOnMaster() || clusterGroup.hostNames().isEmpty()) {
             compute = ignite.compute();
        } else {
             compute = ignite.compute(clusterGroup);
        }
        final IgniteFuture<Object> future = compute.withExecutor(taskId).callAsync(gridCompute::apply);
        return new IgniteComputeFuture(future);
    }

    @Override
    public void close() throws Exception {
        if (ignite != null) {
            ignite.close();
        }
    }

    @Override
    public int concurrency() {
        return ignite.cluster().nodes().size();
    }

}
