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
