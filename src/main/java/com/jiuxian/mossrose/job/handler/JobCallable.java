package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.job.RunnableJob;

import java.io.Serializable;

@FunctionalInterface
public interface JobCallable<T extends RunnableJob, R> extends Serializable {

    R apply(T t);

}
