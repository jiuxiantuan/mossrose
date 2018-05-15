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
import com.jiuxian.mossrose.job.SimpleJob;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;

public class SimpleJobHandler implements JobHandler {

    @Override
    public void handle(JobMeta jobMeta, Ignite ignite) {
        ignite.compute()
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final SimpleJob simpleJob = igniteRemote.services().serviceProxy(jobMeta.getId(), SimpleJob.class, false);
                        simpleJob.executor().execute();
                    }
                });
    }

    @Override
    public Service asService(Object job) {
        return new SimpleJobService((SimpleJob) job);
    }

    class SimpleJobService extends ServiceAdapter implements SimpleJob {

        private SimpleJob simpleJob;

        public SimpleJobService(SimpleJob simpleJob) {
            this.simpleJob = simpleJob;
        }

        @Override
        public Executor executor() {
            return simpleJob.executor();
        }

    }

}
