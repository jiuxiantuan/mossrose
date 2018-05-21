package com.jiuxian.mossrose.job.handler.impl;

import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.handler.Handler;
import com.jiuxian.mossrose.job.handler.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DistributedJobHandler implements Handler<DistributedJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedJobHandler.class);

    @Override
    public void handle(JobExecutor<DistributedJob> jobExecutor) {
        final List<Object> slice = jobExecutor.call(distributedJob -> distributedJob.slicer().slice());
        if (slice != null) {
            final ExecutorService executor = newThreadPool(jobExecutor);

            slice.forEach(item ->
                    executor.submit(() -> jobExecutor.run(
                            distributedJob -> distributedJob.executor().execute(item)
                    ))
            );

            executor.shutdown();
            try {
                while(!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    // Keep waiting
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
