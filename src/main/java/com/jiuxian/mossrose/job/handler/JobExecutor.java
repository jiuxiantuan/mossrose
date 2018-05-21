package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.RunnableJob;

public interface JobExecutor<T extends RunnableJob> {

    <R> R call(JobCallable<T, R> jobCallable);

    void run(JobRunnable<T> jobRunnable);

    int concurrency();

}
