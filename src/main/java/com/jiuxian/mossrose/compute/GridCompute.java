package com.jiuxian.mossrose.compute;

import java.io.Serializable;

@FunctionalInterface
public interface GridCompute extends Serializable {

	void apply();

}
