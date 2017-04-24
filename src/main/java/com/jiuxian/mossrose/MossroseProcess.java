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

import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.IgniteGridComputer;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.quartz.QuartzProcess;
import com.jiuxian.theone.Competitive;
import com.jiuxian.theone.CompetitiveProcess;
import com.jiuxian.theone.zk.ZookeeperCompetitiveImpl;

/**
 * Mossrose basic implementation<br>
 * 
 * Use zookeeper for master election, zookeeper for discovery, and ignite for
 * grid computation
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseProcess extends CompetitiveProcess {

	private final JobOperation jobOperation;
	private final Competitive competitive;
	private final GridComputer gridComputer;

	public MossroseProcess(final QuartzProcess quartzProcess, final Competitive competitive, final GridComputer gridComputer) {
		super(quartzProcess, competitive);
		this.jobOperation = quartzProcess;
		this.competitive = competitive;
		this.gridComputer = gridComputer;
		quartzProcess.setGridComputer(gridComputer);
	}

	/**
	 * @param mossroseConfig
	 *            mossrose configuration
	 */
	public MossroseProcess(final MossroseConfig mossroseConfig) {
		this(new QuartzProcess(mossroseConfig), new ZookeeperCompetitiveImpl(mossroseConfig.getCluster().getDiscoveryZk(), mossroseConfig.getCluster().getName()),
				new IgniteGridComputer(mossroseConfig.getCluster()));
	}

	public JobOperation getJobOperation() {
		return jobOperation;
	}

	public Competitive getCompetitive() {
		return competitive;
	}

	@Override
	public void run() {
		gridComputer.init();
		super.run();
	}

}
