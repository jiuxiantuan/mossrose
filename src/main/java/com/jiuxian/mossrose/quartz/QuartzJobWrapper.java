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
package com.jiuxian.mossrose.quartz;

import com.google.common.base.Stopwatch;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.RunnableJob;
import com.jiuxian.mossrose.job.handler.*;
import javafx.util.Pair;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@DisallowConcurrentExecution
public class QuartzJobWrapper implements Job {

    private boolean runOnMaster;

    private JobMeta jobMeta;

    private Ignite ignite;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobWrapper.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final Stopwatch watch = Stopwatch.createStarted();

        final Pair<Class<RunnableJob>, Handler> classHandlerPair = JobHandlerFactory.getInstance().getMJobHandler(jobMeta.getJobClazz());

        final Class<RunnableJob> jobClass = classHandlerPair.getKey();
        final Handler handler = classHandlerPair.getValue();

        handler.handle(new JobExecutor() {
            @Override
            public Object call(JobCallable jobCallable) {
                return compute().call(new IgniteCallable<Object>() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public Object call() throws Exception {
                        final RunnableJob runnableJob = igniteRemote.services().serviceProxy(jobMeta.getId(), jobClass, false);
                        return jobCallable.apply(runnableJob);
                    }
                });
            }

            @Override
            public void run(JobRunnable jobRunnable) {
                compute().run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final RunnableJob runnableJob = igniteRemote.services().serviceProxy(jobMeta.getId(), jobClass, false);
                        jobRunnable.apply(runnableJob);
                    }
                });
            }
        });
        watch.stop();
        LOGGER.info("Job {} use time: {} ms.", jobMeta.getId(), watch.elapsed(TimeUnit.MILLISECONDS));
    }

    private IgniteCompute compute() {
        final IgniteCluster cluster = ignite.cluster();
        if (!runOnMaster && cluster.nodes().size() > 1) {
            return ignite.compute(cluster.forRemotes());
        }
        return ignite.compute();
    }

    public void setJobMeta(JobMeta jobMeta) {
        this.jobMeta = jobMeta;
    }

    public void setIgnite(Ignite ignite) {
        this.ignite = ignite;
    }

    public void setRunOnMaster(boolean runOnMaster) {
        this.runOnMaster = runOnMaster;
    }
}
