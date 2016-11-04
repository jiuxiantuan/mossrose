package com.jiuxian.mossrose.test;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.base.Splitter;
import com.jiuxian.mossrose.job.DistributedJob;

public class SomeDistributedJob implements DistributedJob<String> {

	@Override
	public List<String> slice() {
		return Splitter.on(" ").splitToList("This is a test on the mossrose distributed job, how are you feeling?");
	}

	@Override
	public void execute(String item) {
		try {
			Thread.sleep(RandomUtils.nextLong(0, 1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread() + " DistributedJob: " + item);
	}

}
