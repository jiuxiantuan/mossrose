package com.jiuxian.mossrose.job.to;

import com.jiuxian.mossrose.spring.SpringContextHolder;

public class SpringBeanObjectResource implements ObjectResource {

	private String beanName;

	public SpringBeanObjectResource() {
		super();
	}

	public SpringBeanObjectResource(String beanName) {
		super();
		this.beanName = beanName;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public Object generate() {
		return SpringContextHolder.getBean(beanName);
	}

	@Override
	public Class<?> clazz() {
		return SpringContextHolder.getBean(beanName).getClass();
	}

}
