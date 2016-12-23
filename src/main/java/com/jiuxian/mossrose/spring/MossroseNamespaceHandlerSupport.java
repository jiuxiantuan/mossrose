package com.jiuxian.mossrose.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MossroseNamespaceHandlerSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new MossroseConfigBean());
		registerBeanDefinitionParser("process", new MossroseProcessBean());
		registerBeanDefinitionParser("ui", new RestMossroseUIBean());
		registerBeanDefinitionParser("springholder", new SpringContextHolderBean());
	}

}
