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
package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.StreamingJob.Streamer;
import com.jiuxian.mossrose.job.to.ObjectContainer;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class StreamingJobHandler extends AbstractJobHandler implements JobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingJobHandler.class);

    @Override
    public void handle(JobMeta jobMeta, Ignite ignite) {
        ignite.compute(select(ignite))
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final Streamer<Serializable> streamer = ObjectContainer.<StreamingJob<Serializable>>get(jobMeta.getId()).streamer();
                        final int concurrency = igniteRemote.cluster().nodes().size() * jobMeta.getThreads();
                        LOGGER.info("Cluster concurrency : {}", concurrency);

                        final Semaphore cycle = new Semaphore(concurrency);
                        final List<IgniteFuture> futures = new ArrayList<>();

                        final IgniteCompute igniteCompute = igniteRemote.compute(select(ignite)).withExecutor(jobMeta.getId());
                        while (streamer.hasNext()) {
                            // execute
                            final Serializable next = streamer.next();
                            try {
                                cycle.acquire();
                                final IgniteFuture<Void> igniteFuture = igniteCompute
                                        .runAsync(() -> {
                                            ObjectContainer.<StreamingJob<Serializable>>get(jobMeta.getId()).executor().execute(next);
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

}
