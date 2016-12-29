package com.jiuxian.mossrose.job;

import java.io.Serializable;
import java.util.List;

public interface MapReduceJob<M extends Serializable, R extends Serializable> extends RunnableJob {

	Mapper<M> mapper();

	Executor<M, R> executor();

	Reducer<R> reducer();

	interface Mapper<M> {
		List<M> map();
	}

	interface Executor<M, R> {
		R execute(M item);
	}

	interface Reducer<R> {
		void reduce(List<R> rs);
	}

}
