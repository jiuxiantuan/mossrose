package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.StreamingJob.Streamer;

public class StreamingJobHandler implements MJobHandler<StreamingJob<Serializable>> {

	@Override
	public void handle(final StreamingJob<Serializable> mJob, final GridComputer gridComputer) {
		final Streamer<Serializable> streamer = mJob.streamer();

		final List<ComputeFuture> futures = Lists.newArrayList();
		while (streamer.hasNext()) {
			final Serializable next = streamer.next();
			futures.add(gridComputer.execute(() -> mJob.executor().execute(next)));
		}
		futures.forEach(ComputeFuture::join);
	}

}
