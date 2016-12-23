package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.to.JobUnit;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class DistributedJobHandler implements MJobHandler<DistributedJob<Serializable>> {

	@Override
	@SuppressWarnings("unchecked")
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		final DistributedJob<Serializable> mJob = (DistributedJob<Serializable>) objectResource.generate();
		final List<Serializable> items = mJob.slicer().slice();
		if (items != null) {
			List<ComputeFuture> futures = items.stream().parallel()
					.map(item -> gridComputer.execute(new JobUnit<Serializable>(objectResource, item)::execute)).collect(Collectors.toList());
			futures.forEach(ComputeFuture::join);
		}

	}

}
