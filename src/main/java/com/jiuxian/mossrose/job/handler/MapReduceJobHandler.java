package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MapReduceJob;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class MapReduceJobHandler implements JobHandler<MapReduceJob<Serializable, Serializable>> {

	@Override
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		@SuppressWarnings("unchecked")
		final MapReduceJob<Serializable, Serializable> mJob = (MapReduceJob<Serializable, Serializable>) objectResource.generate();
		final List<Serializable> items = mJob.mapper().map();
		if (items != null) {
			final List<ComputeFuture> futures = items.stream().parallel()
					.map(item -> gridComputer.execute(jobMeta.getId(), () -> this.runInCluster(objectResource, item))).collect(Collectors.toList());
			final List<Serializable> rs = futures.stream().map(e -> e.join()).collect(Collectors.toList());
			mJob.reducer().reduce(rs);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object runInCluster(ObjectResource objectResource, Serializable data) {
		return ((MapReduceJob<Serializable, Serializable>) objectResource.generate()).executor().execute(data);
	}

}
