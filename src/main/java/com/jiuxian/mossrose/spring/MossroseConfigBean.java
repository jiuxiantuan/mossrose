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

import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

public class MossroseConfigBean extends AbstractSingleBeanDefinitionParser {

	private static final String CLUSTER = "cluster";

	@Override
	protected Class<?> getBeanClass(Element element) {
		return MossroseConfig.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		// Init cluster property
		Element clusterEle = DomUtils.getChildElementByTagName(element, CLUSTER);
		BeanDefinitionBuilder clusterBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(Cluster.class);
		clusterBeanBuilder.addPropertyValue("name", clusterEle.getAttribute("name"));
		clusterBeanBuilder.addPropertyValue("port", clusterEle.getAttribute("port"));
		clusterBeanBuilder.addPropertyValue("loadBalancingMode", clusterEle.getAttribute("load-balancing-mode"));
		clusterBeanBuilder.addPropertyValue("runOnMaster", clusterEle.getAttribute("run-on-master"));
		clusterBeanBuilder.addPropertyValue("discoveryZk", clusterEle.getAttribute("discovery-zk"));

		parserContext.registerBeanComponent(new BeanComponentDefinition(clusterBeanBuilder.getBeanDefinition(), CLUSTER));
		builder.addPropertyReference(CLUSTER, CLUSTER);


		// Init jobs property
		final Element jobsEle = DomUtils.getChildElementByTagName(element, "jobs");
		final List<Element> jobEles = DomUtils.getChildElementsByTagName(jobsEle, "job");
		final ManagedList<BeanReference> jobs = new ManagedList<>();
		for (int i=0;i<jobEles.size();i++) {
			Element jobEle = jobEles.get(i);
			BeanDefinitionBuilder jobBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(JobMeta.class);
			jobBeanBuilder.addPropertyValue("id", jobEle.getAttribute("id"));
			jobBeanBuilder.addPropertyValue("cron", jobEle.getAttribute("cron"));
			jobBeanBuilder.addPropertyValue("group", jobEle.getAttribute("group"));
			jobBeanBuilder.addPropertyValue("description", jobEle.getAttribute("description"));
			jobBeanBuilder.addPropertyValue("main", jobEle.getAttribute("main"));
			jobBeanBuilder.addPropertyValue("jobBeanName", jobEle.getAttribute("job-bean-name"));

			String jobBean = "job" + i;
			parserContext.registerBeanComponent(new BeanComponentDefinition(jobBeanBuilder.getBeanDefinition(), jobBean));
			jobs.add(new RuntimeBeanReference(jobBean));
		}
		builder.addPropertyValue("jobs", jobs);
	}

}
