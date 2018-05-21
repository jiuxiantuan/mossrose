package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.RunnableJob;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface Handler<T extends RunnableJob> {

    void handle(JobExecutor<T> jobExecutor);

    default ExecutorService newThreadPool(JobExecutor jobExecutor) {
        final int concurrency = jobExecutor.concurrency();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(concurrency, concurrency,
                0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1), new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }

}
