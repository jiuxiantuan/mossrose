/**
 * Copyright 2015-2020 jiuxian.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose.job.to;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Throwables;
import com.jiuxian.mossrose.annotation.Singleton;

/**
 * 对象资源实现，通过classname反射获取对象
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class ClassnameObjectResource implements ObjectResource {

	private static ConcurrentHashMap<Class<?>, Object> objects = new ConcurrentHashMap<>();

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
			final Class<?> clazz = clazz();
			if (needToBeSingleton(clazz)) {
				if (!objects.contains(clazz)) {
					objects.putIfAbsent(clazz, clazz.newInstance());
				}
				return objects.get(clazz);
			} else {
				return clazz.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	private boolean needToBeSingleton(Class<?> clazz) {
		return clazz.getAnnotation(Singleton.class) != null;
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
