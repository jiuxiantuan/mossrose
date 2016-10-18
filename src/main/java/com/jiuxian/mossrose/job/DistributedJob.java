package com.jiuxian.mossrose.job;

import java.io.Serializable;
import java.util.List;

public interface DistributedJob<T extends Serializable> {

	List<T> slice();

	void execute(T item);

}
