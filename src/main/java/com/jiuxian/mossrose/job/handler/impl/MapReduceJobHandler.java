package com.jiuxian.mossrose.job.handler.impl;

import com.jiuxian.mossrose.job.MapReduceJob;
import com.jiuxian.mossrose.job.handler.Handler;
import com.jiuxian.mossrose.job.handler.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MapReduceJobHandler implements Handler<MapReduceJob> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final Logger LOGGER = LoggerFactory.getLogger(MapReduceJobHandler.class);

    @Override
    public void handle(JobExecutor<MapReduceJob> jobExecutor) {
        final List<Object> mapResult = jobExecutor.call(mapReduceJob -> mapReduceJob.mapper().map());
        if (mapResult != null) {
            final List<Future> futures = mapResult.parallelStream()
                    .map(item -> EXECUTOR_SERVICE.submit(() -> jobExecutor.call(mapReduceJob -> mapReduceJob.executor().execute(item))))
                    .collect(Collectors.toList());

            final List<Object> executeResults = futures.stream().map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }).collect(Collectors.toList());

            jobExecutor.run(job -> job.reducer().reduce(executeResults));
        }
    }

}
