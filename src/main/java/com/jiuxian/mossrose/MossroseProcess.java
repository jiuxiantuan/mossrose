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
package com.jiuxian.mossrose;

import com.jiuxian.mossrose.cluster.ZookeeperClusterDiscovery;
import com.jiuxian.mossrose.compute.IgniteGridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.quartz.QuartzProcess;
import com.jiuxian.theone.zk.ZookeeperUniqueProcess;

/**
 * Mossrose basic implementation<br>
 * 
 * Use zookeeper for master election, zookeeper for discovery, and ignite for
 * grid computation
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseProcess extends ZookeeperUniqueProcess {

	private JobOperation jobOperation;

	public MossroseProcess(QuartzProcess quartzProcess, String zks, String group) {
		super(quartzProcess, zks, group);
		this.jobOperation = quartzProcess;
	}

	/**
	 * @param mossroseConfig
	 *            mossrose configuration
	 * @param zks
	 *            zookeeper address
	 */
	public MossroseProcess(MossroseConfig mossroseConfig, String zks) {
		this(new QuartzProcess(mossroseConfig,
				new IgniteGridComputer(mossroseConfig.getCluster(), new ZookeeperClusterDiscovery(mossroseConfig.getCluster().getName(), zks))), zks,
				mossroseConfig.getCluster().getName());
	}

	public JobOperation getJobOperation() {
		return jobOperation;
	}

}
