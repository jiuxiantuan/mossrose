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

import org.apache.ignite.configuration.IgniteConfiguration;

import com.google.common.collect.Lists;

import jline.internal.Preconditions;

/**
 * Registry for user-custom IgniteConfigurationRender
 * 
 * <pre>
 * IgniteConfigurationRender userCustomConf = new UserCustomIgniteConfigurationRender();
 * IgniteConfigurationRenderRegistry.regiter(userCustomConf);
 * </pre>
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class IgniteConfigurationRenderRegistry {

	private static final List<IgniteConfigurationRender> RENDERS = Lists.newCopyOnWriteArrayList();

	public static void register(IgniteConfigurationRender render) {
		RENDERS.add(Preconditions.checkNotNull(render));
	}

	public static void render(IgniteConfiguration igniteConfiguration) {
		for (IgniteConfigurationRender render : RENDERS) {
			render.render(igniteConfiguration);
		}
	}

}
