package com.jiuxian.mossrose.compute;

public interface GridComputer extends AutoCloseable {
	
	void execute(GridCompute gridCompute);
	
}
