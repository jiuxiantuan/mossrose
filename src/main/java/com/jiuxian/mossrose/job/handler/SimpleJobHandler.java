package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.SimpleJob;
import com.jiuxian.mossrose.job.to.JobUnit;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class SimpleJobHandler implements MJobHandler<SimpleJob<Serializable>> {

	@Override
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		gridComputer.execute(new JobUnit<Serializable>(objectResource, "execute")::execute);
	}

}
