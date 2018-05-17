package com.jiuxian.mossrose.job.handler.impl;

import com.jiuxian.mossrose.job.SimpleJob;
import com.jiuxian.mossrose.job.handler.Handler;
import com.jiuxian.mossrose.job.handler.JobExecutor;

public class SimpleJobHandler implements Handler<SimpleJob> {

    @Override
    public void handle(JobExecutor<SimpleJob> jobExecutor) {
        jobExecutor.run(simpleJob -> simpleJob.executor().execute());
    }

}
