package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MJob;
import com.jiuxian.mossrose.job.to.ObjectResource;

public interface MJobHandler<T extends MJob<Serializable>> {

	void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer);

}
