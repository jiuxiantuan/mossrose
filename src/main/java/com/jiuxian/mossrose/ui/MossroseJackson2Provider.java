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

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class MossroseJackson2Provider extends ResteasyJackson2Provider {

	protected MossroseJackson2Provider() {
		super();
		configure(SerializationFeature.INDENT_OUTPUT, true);
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		_mapperConfig.getConfiguredMapper().getSerializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA));

	}

}
