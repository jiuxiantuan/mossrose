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

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jiuxian.mossrose.config.MossroseConfig;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster;
import com.jiuxian.mossrose.config.MossroseConfig.Cluster.LoadBalancingMode;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;

public class MossroseConfigBean extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return MossroseConfig.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		// Init cluster property
		Element clusterEle = DomUtils.getChildElementByTagName(element, "cluster");
		Cluster cluster = new Cluster();
		cluster.setName(clusterEle.getAttribute("name"));
		cluster.setPort(Integer.parseInt(clusterEle.getAttribute("port")));
		cluster.setLoadBalancingMode(LoadBalancingMode.valueOf(clusterEle.getAttribute("load-balancing-mode")));
		builder.addPropertyValue("cluster", cluster);

		// Init jobs property
		Element jobsEle = DomUtils.getChildElementByTagName(element, "jobs");
		List<Element> jobEles = DomUtils.getChildElementsByTagName(jobsEle, "job");
		final List<JobMeta> jobs = Lists.newArrayList();
		for (Element jobEle : jobEles) {
			JobMeta job = new JobMeta();
			job.setId(jobEle.getAttribute("id"));
			job.setCron(jobEle.getAttribute("cron"));
			job.setGroup(jobEle.getAttribute("group"));
			job.setDescription(jobEle.getAttribute("description"));

			String mainClazz = jobEle.getAttribute("main");
			String springRef = jobEle.getAttribute("job-bean-name");
			if (Strings.isNullOrEmpty(mainClazz) && Strings.isNullOrEmpty(springRef)) {
				parserContext.getReaderContext().error("one of the 'main' or 'job-bean-name' attributes is required", jobEle);
				continue;
			}

			job.setMain(mainClazz);
			job.setJobBeanName(springRef);
			jobs.add(job);
		}
		builder.addPropertyValue("jobs", jobs);
	}

}
