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
import java.util.List;
import java.util.stream.Collectors;

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.to.JobUnit;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class DistributedJobHandler implements MJobHandler<DistributedJob<Serializable>> {

	@Override
	@SuppressWarnings("unchecked")
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		final DistributedJob<Serializable> mJob = (DistributedJob<Serializable>) objectResource.generate();
		final List<Serializable> items = mJob.slicer().slice();
		if (items != null) {
			List<ComputeFuture> futures = items.stream().parallel()
					.map(item -> gridComputer.execute(new JobUnit<Serializable>(objectResource, item)::execute)).collect(Collectors.toList());
			futures.forEach(ComputeFuture::join);
		}

	}

}