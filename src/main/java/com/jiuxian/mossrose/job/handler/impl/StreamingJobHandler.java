package com.jiuxian.mossrose.job.handler.impl;

import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.handler.Handler;
import com.jiuxian.mossrose.job.handler.JobExecutor;
import com.jiuxian.mossrose.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StreamingJobHandler implements Handler<StreamingJob> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingJobHandler.class);

    @Override
    public void handle(JobExecutor<StreamingJob> jobExecutor) {
        final List<Future> futures = new ArrayList<>();

        Tuple<Object, Object> dataMarkPair = jobExecutor.call(streamingJob -> streamingJob.streamer().next(null));
        while (dataMarkPair != null) {
            final Object data = dataMarkPair.getFirst();
            final Object mark = dataMarkPair.getSecond();

            futures.add(EXECUTOR_SERVICE.submit(() -> jobExecutor.run(streamingJob -> streamingJob.executor().execute(data))));

            dataMarkPair = jobExecutor.call(streamingJob -> streamingJob.streamer().next(mark));
        }

        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

    }

}
