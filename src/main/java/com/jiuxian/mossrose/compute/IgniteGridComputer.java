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
package com.jiuxian.mossrose.compute;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.compute.ComputeTaskFuture;

import com.google.common.collect.Lists;
import com.jiuxian.mossrose.cluster.ClusterDiscovery;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster;

public class IgniteGridComputer implements GridComputer {

	private final Ignite ignite;

	public IgniteGridComputer(Cluster cluster, ClusterDiscovery clusterDiscovery) {
		ignite = IgniteClusterBuilder.build(cluster, clusterDiscovery);
	}

	@Override
	public void execute(ComputeUnit gridCompute) {
		ignite.compute().run(gridCompute::apply);
	}

	@Override
	public void close() throws Exception {
		if (ignite != null) {
			ignite.close();
		}
	}

	@Override
	public void execute(List<ComputeUnit> gridComputes) {
		IgniteCompute compute = ignite.compute().withAsync();
		final List<ComputeTaskFuture<?>> futs = Lists.newArrayList();
		gridComputes.forEach(e -> {
			compute.run(e::apply);
			futs.add(compute.future());
		});
		futs.forEach(ComputeTaskFuture::get);
	}

}
