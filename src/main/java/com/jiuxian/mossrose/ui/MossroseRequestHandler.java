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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.jiuxian.mossrose.JobOperation;
import com.jiuxian.mossrose.JobOperation.JobRuntimeInfo;

/**
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
@Path("")
public class MossroseRequestHandler {

	private JobOperation jobOperation;

	protected MossroseRequestHandler(JobOperation jobOperation) {
		super();
		this.jobOperation = jobOperation;
	}

	@GET
	@Path("all")
	@Produces("application/json")
	public Response<List<JobRuntimeInfo>> allJobInfo() {
		return new Response<List<JobRuntimeInfo>>(0, jobOperation.allJobs());
	}

	@GET
	@Path("/all/pause")
	@Produces("application/json")
	public Response<Object> pauseAllJob() {
		jobOperation.pauseAllJob();
		return new Response<Object>(0, null);
	}

	@GET
	@Path("/all/resume")
	@Produces("application/json")
	public Response<Object> resumeAllJob() {
		jobOperation.resumeAllJob();
		return new Response<Object>(0, null);
	}

	@GET
	@Path("/{group}/{jobId}")
	@Produces("application/json")
	public Response<JobRuntimeInfo> jobInfo(@PathParam(value = "group") String group, @PathParam("jobId") String jobId) {
		return new Response<JobRuntimeInfo>(0, jobOperation.jobInfo(group, jobId));
	}

	@GET
	@Path("/{group}/{jobId}/pause")
	@Produces("application/json")
	public Response<Object> pauseJob(@PathParam(value = "group") String group, @PathParam("jobId") String jobId) {
		jobOperation.pauseJob(group, jobId);
		return new Response<Object>(0, null);
	}

	@GET
	@Path("/{group}/{jobId}/resume")
	@Produces("application/json")
	public Response<Object> resumeJob(@PathParam(value = "group") String group, @PathParam("jobId") String jobId) {
		jobOperation.resumeJob(group, jobId);
		return new Response<Object>(0, null);
	}

	@GET
	@Path("/{group}/{jobId}/run")
	@Produces("application/json")
	public Response<Object> runJob(@PathParam(value = "group") String group, @PathParam("jobId") String jobId) {
		jobOperation.runJobNow(group, jobId);
		return new Response<Object>(0, null);
	}

}
