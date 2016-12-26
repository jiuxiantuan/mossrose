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

import java.io.Serializable;

import com.jiuxian.mossrose.job.MJob;

/**
 * 任务数据单元
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 * @param <T>
 */
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