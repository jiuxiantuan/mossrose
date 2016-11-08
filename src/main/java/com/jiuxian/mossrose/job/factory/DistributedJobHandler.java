package com.jiuxian.mossrose.job.factory;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.jiuxian.mossrose.compute.ComputeUnit;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.DistributedJob.Executor;

public class DistributedJobHandler implements MJobHandler<DistributedJob<Serializable>> {

	@Override
	public void handle(DistributedJob<Serializable> mJob, GridComputer gridComputer) {
		final List<Serializable> items = mJob.slicer().slice();
		if (items != null) {
			final Executor<Serializable> executor = mJob.executor();
			gridComputer.execute(items.stream().<ComputeUnit> map(e -> () -> executor.execute(e)).collect(Collectors.toList()));
		}
	}

}
