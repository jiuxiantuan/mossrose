package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.job.DistributedJob;

public class DistributedJobHandler implements MJobHandler<DistributedJob<Serializable>> {

	@Override
	public void handle(DistributedJob<Serializable> mJob, GridComputer gridComputer) {
		final List<Serializable> items = mJob.slicer().slice();
		if (items != null) {
			List<ComputeFuture> futures = items.stream().parallel().map(item -> gridComputer.execute(() -> mJob.executor().execute(item)))
					.collect(Collectors.toList());
			futures.forEach(ComputeFuture::join);
		}
	}

}
