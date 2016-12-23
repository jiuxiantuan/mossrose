package com.jiuxian.mossrose.job.to;

import com.google.common.base.Throwables;

public class ClassnameObjectResource implements ObjectResource {

	public ClassnameObjectResource() {
		super();
	}

	public ClassnameObjectResource(String classname) {
		super();
		this.classname = classname;
	}

	private String classname;

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	@Override
	public Object generate() {
		try {
			return clazz().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Class<?> clazz() {
		try {
			return Class.forName(classname);
		} catch (ClassNotFoundException e) {
			throw Throwables.propagate(e);
		}
	}

}
