package com.jiuxian.mossrose.job.factory;

import java.io.Serializable;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.StreamingJob.Streamer;

public class StreamingJobHandler implements MJobHandler<StreamingJob<Serializable>> {

	@Override
	public void handle(StreamingJob<Serializable> mJob, GridComputer gridComputer) {
		final Streamer<Serializable> streamer = mJob.streamer();
		while (streamer.hasNext()) {
			gridComputer.execute(() -> mJob.executor().execute(streamer.next()));
		}
	}

}
