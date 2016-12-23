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
package com.jiuxian.mossrose.ui;

import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.MossroseProcess;
import com.jiuxian.theone.Competitive;

/**
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class RestMossroseUI implements AutoCloseable {

	private NettyJaxrsServer server;

	private static final Logger LOGGER = LoggerFactory.getLogger(RestMossroseUI.class);
	
	public RestMossroseUI(final MossroseProcess mossroseProcess, final int port) {
		this(mossroseProcess.getJobOperation(), mossroseProcess.getCompetitive(), port);
	}

	public RestMossroseUI(final JobOperation jobOperation, final Competitive competitive, final int port) {
		super();
		final ResteasyDeployment deployment = new ResteasyDeployment();
		deployment.setSecurityEnabled(true);
		deployment.setApplication(new Application() {

			private Set<Object> singletons = Sets.<Object> newHashSet(new MossroseRequestHandler(jobOperation));

			@Override
			public Set<Object> getSingletons() {
				return singletons;
			}

		});
		deployment.setProviders(Lists.<Object> newArrayList(new MasterRouting(competitive), new MossroseJackson2Provider()));

		final NettyJaxrsServer server = new NettyJaxrsServer();
		server.setDeployment(deployment);
		server.setExecutorThreadCount(1);
		server.setPort(port);
		server.setRootResourcePath("");
		server.setSecurityDomain(null);

		server.start();

		LOGGER.info("Server startup at port [{}].", port);
	}

	@Override
	public void close() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

}
