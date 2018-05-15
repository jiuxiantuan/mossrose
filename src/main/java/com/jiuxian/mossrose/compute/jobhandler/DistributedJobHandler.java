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
package com.jiuxian.mossrose.compute.jobhandler;

import com.jiuxian.mossrose.compute.ServiceAdapter;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.DistributedJob;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DistributedJobHandler implements JobHandler {

    @Override
    public void handle(final JobMeta jobMeta, Ignite ignite) {
        ignite.compute()
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final DistributedJob<Serializable> distributedJob = igniteRemote.services().serviceProxy(jobMeta.getId(), DistributedJob.class, false);
                        final List<Serializable> items = distributedJob.slicer().slice();
                        if (items != null) {
                            final List<IgniteFuture<Void>> igniteFutures = items.stream().parallel()
                                    .map(item ->
                                            igniteRemote.compute()
                                                    .runAsync(new IgniteRunnable() {

                                                        @IgniteInstanceResource
                                                        private Ignite igniteRemoteAgain;

                                                        @Override
                                                        public void run() {
                                                            final DistributedJob<Serializable> distributedJob = igniteRemoteAgain.services().serviceProxy(jobMeta.getId(), DistributedJob.class, false);
                                                            distributedJob.executor().execute(item);
                                                        }
                                                    })
                                    )
                                    .collect(Collectors.toList());


                            igniteFutures.forEach(IgniteFuture::get);
                        }
                    }
                });

    }

    @Override
    public Service asService(Object job) {
        return new DistributedJobService((DistributedJob) job);
    }

    class DistributedJobService extends ServiceAdapter implements DistributedJob<Serializable> {

        private DistributedJob<Serializable> distributedJob;

        public DistributedJobService(DistributedJob<Serializable> distributedJob) {
            this.distributedJob = distributedJob;
        }

        @Override
        public Slicer<Serializable> slicer() {
            return distributedJob.slicer();
        }

        @Override
        public Executor<Serializable> executor() {
            return distributedJob.executor();
        }
    }

}
