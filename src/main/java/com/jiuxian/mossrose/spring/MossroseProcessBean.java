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
package com.jiuxian.mossrose.spring;

import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.jiuxian.mossrose.MossroseProcess;
import com.jiuxian.mossrose.compute.IgniteConfigurationRender;
import com.jiuxian.mossrose.compute.IgniteConfigurationRenderRegistry;

public class MossroseProcessBean extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return MossroseProcess.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		preLoad();

		String mossroseConfigRef = element.getAttribute("mossrose-config-ref");
		builder.addConstructorArgReference(mossroseConfigRef);
	}

	private void preLoad() {
		IgniteConfigurationRenderRegistry.register(new IgniteConfigurationRender() {

			@Override
			public void render(IgniteConfiguration igniteConfiguration) {
				// igniteConfiguration.setMarshaller(new BinaryMarshaller());

				CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>();
				cacheConfiguration.setBackups(0);
				igniteConfiguration.setCacheConfiguration(cacheConfiguration);
			}
		});
	}

}
