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

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jiuxian.theone.Competitive;
import com.jiuxian.theone.util.NetworkUtils;

/**
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
@PreMatching
public class MasterRouting implements ContainerRequestFilter {

	private Competitive competitive;

	protected MasterRouting(Competitive competitive) {
		super();
		this.competitive = competitive;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterRouting.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		final String currentLocker = competitive.currentLocker();
		final String localIp = NetworkUtils.getLocalIp();
		if (!Objects.equals(currentLocker, localIp)) {
			final URI masterLocation = requestContext.getUriInfo().getAbsolutePathBuilder().host(currentLocker).build();
			LOGGER.info("Redirect url to {}.", masterLocation);
			final Response response = javax.ws.rs.core.Response.seeOther(masterLocation).build();
			requestContext.abortWith(response);
		}
	}

}
