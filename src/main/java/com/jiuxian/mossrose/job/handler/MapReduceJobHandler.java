package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MapReduceJob;
import com.jiuxian.mossrose.job.to.ObjectResource;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MapReduceJobHandler implements JobHandler<MapReduceJob<Serializable, Serializable>> {

	@Override
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		// map
		gridComputer.execute(jobMeta.getId(), () -> {
			@SuppressWarnings("unchecked") final MapReduceJob<Serializable, Serializable> mJob = (MapReduceJob<Serializable, Serializable>) objectResource.generate();
			final List<Serializable> items = mJob.mapper().map();
			if (items != null) {
				// execute
				final List<ComputeFuture> futures = items.stream().parallel()
						.map(item -> gridComputer.execute(jobMeta.getId(), () -> ((MapReduceJob<Serializable, Serializable>) objectResource.generate()).executor().execute(item))).collect(Collectors.toList());

				// reduce
				final List<Serializable> rs = futures.stream().map(e -> e.join()).collect(Collectors.toList());
				mJob.reducer().reduce(rs);
			}
			return null;
		}).join();
	}

}
