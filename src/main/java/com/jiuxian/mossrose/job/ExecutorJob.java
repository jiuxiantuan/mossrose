package com.jiuxian.mossrose.job;

import java.io.Serializable;

public interface ExecutorJob<T extends Serializable> extends RunnableJob {

	Executor<T> executor();

	interface Executor<T> {
		void execute(T item);
	}

}
