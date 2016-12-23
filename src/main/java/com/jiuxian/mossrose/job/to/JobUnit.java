package com.jiuxian.mossrose.job.to;

import java.io.Serializable;

import com.jiuxian.mossrose.job.MJob;

public class JobUnit<T extends Serializable> {

	private ObjectResource objectResource;

	private T argument;

	public JobUnit(ObjectResource objectResource, T argument) {
		super();
		this.objectResource = objectResource;
		this.argument = argument;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		((MJob<T>) objectResource.generate()).executor().execute(argument);
	}

}
