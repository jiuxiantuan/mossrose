package com.jiuxian.mossrose.job.handler;

import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MapReduceJob;
import com.jiuxian.mossrose.job.to.ObjectContainer;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapReduceJobHandler extends AbstractJobHandler implements JobHandler {

    @Override
    public void handle(JobMeta jobMeta, Ignite ignite) {
        // map
        ignite.compute(select(ignite))
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

            @IgniteInstanceResource
            private Ignite igniteRemote;

            @Override
            public void run() {
                final String s = igniteRemote.toString();
                System.err.println(s.substring(s.length() - 9));

                final MapReduceJob<Serializable, Serializable> mJob = ObjectContainer.get(jobMeta.getId());
                final List<Serializable> items = mJob.mapper().map();
                if (items != null) {
                    // execute
                    final Collection<IgniteCallable<Serializable>> jobs = new ArrayList<>();
                    items.forEach(item -> jobs.add(() -> ObjectContainer.<MapReduceJob<Serializable, Serializable>>get(jobMeta.getId()).executor().execute(item)));

                    final Collection<Serializable> results = igniteRemote.compute(select(ignite))
                            .withExecutor(jobMeta.getId())
                            .call(jobs);

                    mJob.reducer().reduce(new ArrayList<>(results));
                }
            }
        });

    }

}
