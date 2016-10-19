package com.jiuxian.mossrose.job;

import java.io.Serializable;
import java.util.List;

/**
 * 分布式任务，可以将密集任务分解后在集群中执行
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 * @param <T>
 */
public interface DistributedJob<T extends Serializable> {

	List<T> slice();

	void execute(T item);

}
