package com.jiuxian.mossrose.test;

import java.util.UUID;

import com.jiuxian.mossrose.job.SimpleJob;

public class SomeJob implements SimpleJob {

	@Override
	public void execute() {
		System.out.println("SimpleJob: " + UUID.randomUUID());
	}

}
