package com.jiuxian.mossrose.job;

import java.io.Serializable;

public interface StreamingJob<T extends Serializable> extends MJob {

	Streamer<T> streamer();

	Executor<T> executor();

	interface Streamer<T> {
		boolean hasNext();

		T next();
	}

	interface Executor<T> {
		void execute(T item);
	}

}
