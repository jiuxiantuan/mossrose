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
import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.StreamingJob.Streamer;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class StreamingJobHandler implements JobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingJobHandler.class);

    @Override
    public void handle(JobMeta jobMeta, Ignite ignite) {
        ignite.compute()
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final StreamingJob<Serializable> streamingJob = igniteRemote.services().serviceProxy(jobMeta.getId(), StreamingJob.class, false);
                        final int concurrency = igniteRemote.cluster().nodes().size() * jobMeta.getThreads();
                        LOGGER.info("Cluster concurrency : {}", concurrency);

                        final Semaphore cycle = new Semaphore(concurrency);
                        final List<IgniteFuture> futures = new ArrayList<>();

                        final IgniteCompute igniteCompute = igniteRemote.compute().withExecutor(jobMeta.getId());

                        final Streamer<Serializable> streamer = streamingJob.streamer();
                        while (streamer.hasNext()) {
                            // execute
                            final Serializable item = streamer.next();
                            try {
                                cycle.acquire();
                                final IgniteFuture<Void> igniteFuture = igniteCompute
                                        .runAsync(new IgniteRunnable() {

                                            @IgniteInstanceResource
                                            private Ignite igniteRemoteAgain;

                                            @Override
                                            public void run() {
                                                final StreamingJob<Serializable> streamingJobInner = igniteRemoteAgain.services().serviceProxy(jobMeta.getId(), StreamingJob.class, false);
                                                streamingJobInner.executor().execute(item);
                                            }
                                        });
                                futures.add(igniteFuture);
                                igniteFuture.listen((future) -> {
                                    cycle.release();
                                });
                            } catch (InterruptedException e) {
                            }

                        }

                        futures.forEach(IgniteFuture::get);
                    }
                });

    }

    @Override
    public Service asService(Object job) {
        return new StreamingJobService((StreamingJob) job);
    }

    class StreamingJobService extends ServiceAdapter implements StreamingJob<Serializable> {

        private StreamingJob<Serializable> streamingJob;

        public StreamingJobService(StreamingJob<Serializable> streamingJob) {
            this.streamingJob = streamingJob;
        }

        @Override
        public Streamer<Serializable> streamer() {
            return streamingJob.streamer();
        }

        @Override
        public Executor<Serializable> executor() {
            return streamingJob.executor();
        }
    }
}
