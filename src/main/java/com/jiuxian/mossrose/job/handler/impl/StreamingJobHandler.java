package com.jiuxian.mossrose.job.handler.impl;

import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.handler.Handler;
import com.jiuxian.mossrose.job.handler.JobExecutor;
import com.jiuxian.mossrose.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamingJobHandler implements Handler<StreamingJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingJobHandler.class);

    @Override
    public void handle(JobExecutor<StreamingJob> jobExecutor) {
        final ExecutorService executor = newThreadPool(jobExecutor);

        Tuple<Object, Object> dataMarkPair = jobExecutor.call(streamingJob -> streamingJob.streamer().next(null));
        while (dataMarkPair != null) {
            final Object data = dataMarkPair.getFirst();
            final Object mark = dataMarkPair.getSecond();

            executor.submit(() -> jobExecutor.run(streamingJob -> streamingJob.executor().execute(data)));

            dataMarkPair = jobExecutor.call(streamingJob -> streamingJob.streamer().next(mark));
        }

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
