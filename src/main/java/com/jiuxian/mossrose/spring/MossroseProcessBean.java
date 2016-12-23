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

		String zks = element.getAttribute("zks");
		builder.addConstructorArgValue(zks);
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
