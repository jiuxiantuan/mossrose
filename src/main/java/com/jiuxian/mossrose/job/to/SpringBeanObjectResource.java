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

import com.jiuxian.mossrose.spring.SpringContextHolder;

/**
 * 对象资源实现，从Spring容器中获取对象
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class SpringBeanObjectResource implements ObjectResource {

	private String beanName;

	public SpringBeanObjectResource(String beanName) {
		super();
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
