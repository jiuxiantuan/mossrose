package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.SimpleJob;

public class SimpleJobHandler implements MJobHandler<SimpleJob> {

	@Override
	public void handle(SimpleJob mJob, GridComputer gridComputer) {
		gridComputer.execute(mJob::execute);
	}

}
