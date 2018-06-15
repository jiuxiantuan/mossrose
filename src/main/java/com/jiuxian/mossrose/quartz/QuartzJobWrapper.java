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
import com.jiuxian.mossrose.job.handler.JobHandler;
import com.jiuxian.mossrose.job.handler.JobHandlerFactory;
import com.jiuxian.mossrose.job.to.ObjectContainer;
import org.apache.ignite.Ignite;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@DisallowConcurrentExecution
public class QuartzJobWrapper implements Job {

    private JobMeta jobMeta;

    private Ignite ignite;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobWrapper.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            final Stopwatch watch = Stopwatch.createStarted();
            final JobHandler mJobHandler = JobHandlerFactory.getInstance()
                    .getMJobHandler(ObjectContainer.getClazz(jobMeta.getId()));
            mJobHandler.handle(jobMeta, ignite);
            watch.stop();
            LOGGER.info("Job {} use time: {} ms.", jobMeta.getId(), watch.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            LOGGER.error("Error while executing job " + context.getJobDetail().getKey(), e);
        }
    }

    public void setJobMeta(JobMeta jobMeta) {
        this.jobMeta = jobMeta;
    }

    public void setIgnite(Ignite ignite) {
        this.ignite = ignite;
    }
}
