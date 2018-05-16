package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.SimpleJob;

public class SimpleJobHandler implements Handler<SimpleJob> {

    @Override
    public void handle(JobExecutor<SimpleJob> jobProvider) {
        jobProvider.run(simpleJob -> simpleJob.executor().execute());
    }

}
