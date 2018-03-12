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
package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.SimpleJob;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class SimpleJobHandler implements JobHandler<SimpleJob> {

	@Override
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		gridComputer.execute(jobMeta.getId(), () -> this.runInCluster(objectResource, null)).join();
	}

	@Override
	public Object runInCluster(ObjectResource objectResource, Serializable data) {
		((SimpleJob) objectResource.generate()).executor().execute();
		return null;
	}

}
