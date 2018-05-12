package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MapReduceJob;
import com.jiuxian.mossrose.job.to.ObjectContainer;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MapReduceJobHandler implements JobHandler {

    @Override
    public void handle(JobMeta jobMeta) {
        // map
        ObjectContainer.getGridComputer().execute(jobMeta.getId(), () -> {
            @SuppressWarnings("unchecked") final MapReduceJob<Serializable, Serializable> mJob = ObjectContainer.get(jobMeta.getId());
            final List<Serializable> items = mJob.mapper().map();
            if (items != null) {
                // execute
                final List<ComputeFuture> futures = items.stream().parallel()
                        .map(item -> ObjectContainer.getGridComputer()
                                .execute(jobMeta.getId(),
                                        () -> ObjectContainer.<MapReduceJob<Serializable, Serializable>>get(jobMeta.getId()).executor().execute(item)))
                        .collect(Collectors.toList());

                // reduce
                final List<Serializable> rs = futures.stream().map(e -> e.join()).collect(Collectors.toList());
                mJob.reducer().reduce(rs);
            }
            return null;
        }).join();
    }

}
