package com.jiuxian.mossrose.test;

import java.util.UUID;

import com.jiuxian.mossrose.annotation.Singleton;
import com.jiuxian.mossrose.job.SimpleJob;

@Singleton
public class SomeJob implements SimpleJob {

	@Override
	public Executor executor() {
		return new Executor() {

			@Override
			public void execute() {
				System.out.println(this.getClass());
				System.out.println(Thread.currentThread() + " SimpleJob: " + UUID.randomUUID());
			}
		};
	}

}
