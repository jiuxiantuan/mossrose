package com.jiuxian.mossrose.job;

import java.io.Serializable;

public interface StreamingJob<T extends Serializable> extends MJob<T> {

	Streamer<T> streamer();

	interface Streamer<T> {
		boolean hasNext();

		T next();
	}

}
