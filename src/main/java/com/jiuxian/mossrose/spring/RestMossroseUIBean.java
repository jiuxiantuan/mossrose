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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.jiuxian.mossrose.ui.RestMossroseUI;

public class RestMossroseUIBean extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return RestMossroseUI.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String mossroseProcessRef = element.getAttribute("mossrose-process-ref");
		builder.addConstructorArgReference(mossroseProcessRef);

		String port = element.getAttribute("port");
		builder.addConstructorArgValue(port);
	}

}
