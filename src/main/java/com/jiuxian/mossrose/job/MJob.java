package com.jiuxian.mossrose.job;

import java.io.Serializable;

public interface MJob<T extends Serializable> {

	Executor<T> executor();

	interface Executor<T> {
		void execute(T item);
	}

}
