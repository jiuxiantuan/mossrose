package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.RunnableJob;

public interface Handler<T extends RunnableJob> {

    void handle(JobExecutor<T> jobExecutor);

}
