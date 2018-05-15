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
package com.jiuxian.mossrose.compute.jobhandler;

import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import org.apache.ignite.Ignite;
import org.apache.ignite.services.Service;

public interface JobHandler {

	void handle(JobMeta jobMeta, Ignite ignite);

	Service asService(Object job);
	
}
