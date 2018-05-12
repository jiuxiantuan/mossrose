/**
 * Copyright 2015-2020 jiuxian.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.DistributedJob;
import com.jiuxian.mossrose.job.to.ObjectContainer;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DistributedJobHandler implements JobHandler {

    @Override
    @SuppressWarnings("unchecked")
    public void handle(final JobMeta jobMeta) {
        // Slice
        ObjectContainer.getGridComputer().execute(jobMeta.getId(), () -> {
            final List<Serializable> items = ObjectContainer.<DistributedJob<Serializable>>get(jobMeta.getId()).slicer().slice();
            if (items != null) {
                // Execute
                final List<ComputeFuture> futures = items.stream().parallel().map(
                        item -> ObjectContainer.getGridComputer().execute(
                                jobMeta.getId(), () -> {
                                    ObjectContainer.<DistributedJob<Serializable>>get(jobMeta.getId()).executor().execute(item);
                                    return null;
                                })).collect(Collectors.toList());
                futures.forEach(ComputeFuture::join);
            }
            return null;
        }).join();

    }

}
