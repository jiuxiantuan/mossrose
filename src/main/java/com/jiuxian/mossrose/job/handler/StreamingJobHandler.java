package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.StreamingJob.Streamer;
import com.jiuxian.mossrose.job.to.JobUnit;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class StreamingJobHandler implements MJobHandler<StreamingJob<Serializable>> {

	@Override
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		@SuppressWarnings("unchecked")
		final StreamingJob<Serializable> mJob = (StreamingJob<Serializable>) objectResource.generate();
		final Streamer<Serializable> streamer = mJob.streamer();

		final List<ComputeFuture> futures = Lists.newArrayList();
		while (streamer.hasNext()) {
			final Serializable next = streamer.next();
			futures.add(gridComputer.execute(new JobUnit<Serializable>(objectResource, next)::execute));
		}
		futures.forEach(ComputeFuture::join);
	}

}
