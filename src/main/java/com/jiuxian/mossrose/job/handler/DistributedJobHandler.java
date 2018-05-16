package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.DistributedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DistributedJobHandler implements Handler<DistributedJob> {


    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedJobHandler.class);

    @Override
    public void handle(JobExecutor<DistributedJob> jobJobExecutor) {

        final List<Object> slice = jobJobExecutor.call(distributedJob -> distributedJob.slicer().slice());


        if (slice != null) {
            final List<Future> futures = slice.parallelStream().map(item ->
                    EXECUTOR_SERVICE.submit(() -> jobJobExecutor.run(
                            distributedJob -> distributedJob.executor().execute(item)
                    ))
            ).collect(Collectors.toList());

            futures.forEach(future -> {
                try {
                    future.get();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
        }
    }
}
