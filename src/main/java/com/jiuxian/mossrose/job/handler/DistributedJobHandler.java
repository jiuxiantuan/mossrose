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
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.to.ObjectContainer;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DistributedJobHandler extends AbstractJobHandler implements JobHandler {

    @Override
    public void handle(final JobMeta jobMeta, Ignite ignite) {
        ignite.compute(select(ignite))
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final String s = igniteRemote.toString();
                        System.err.println(s.substring(s.length() - 9));

                        final List<Serializable> items = ObjectContainer.<DistributedJob<Serializable>>get(jobMeta.getId()).slicer().slice();
                        if (items != null) {
                            // Execute
                            Collection<IgniteRunnable> jobs = new ArrayList<>();
                            items.forEach(item -> {
                                jobs.add(() -> {
                                    ObjectContainer.<DistributedJob<Serializable>>get(jobMeta.getId()).executor().execute(item);
                                });
                            });
                            igniteRemote.compute(select(ignite))
                                    .withExecutor(jobMeta.getId())
                                    .run(jobs);
                        }
                    }
                });

    }

}
