package com.jiuxian.mossrose.job.factory;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.job.MJob;

public interface MJobHandler<T extends MJob> {

	void handle(T mJob, GridComputer gridComputer);

}
