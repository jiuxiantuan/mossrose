package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.RunnableJob;

import java.io.Serializable;

@FunctionalInterface
public interface JobRunnable<T extends RunnableJob> extends Serializable {

    void apply(T job);

}
