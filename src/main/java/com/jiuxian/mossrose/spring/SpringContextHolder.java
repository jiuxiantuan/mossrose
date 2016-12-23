package com.jiuxian.mossrose.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware {

	private static ApplicationContext staticApplicationContext;

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) staticApplicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return staticApplicationContext.getBean(clazz);
	}
	
	public static <T> Map<String, T> getBeans(Class<T> clazz) {
		return staticApplicationContext.getBeansOfType(clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHolder.staticApplicationContext = applicationContext;
	}

}
