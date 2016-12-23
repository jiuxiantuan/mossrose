package com.jiuxian.mossrose.test;

import java.util.UUID;

import com.jiuxian.mossrose.job.SimpleJob;

public class SomeJob implements SimpleJob<String> {

	@Override
	public Executor<String> executor() {
		return new Executor<String>() {

			@Override
			public void execute(String item) {
				System.out.println(Thread.currentThread() + " SimpleJob: " + UUID.randomUUID());
			}
		};
	}

}
