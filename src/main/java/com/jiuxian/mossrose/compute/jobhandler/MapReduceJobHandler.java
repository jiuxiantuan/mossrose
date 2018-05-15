package com.jiuxian.mossrose.compute.jobhandler;

import com.jiuxian.mossrose.compute.ServiceAdapter;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.MapReduceJob;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapReduceJobHandler implements JobHandler {

    @Override
    public void handle(JobMeta jobMeta, Ignite ignite) {
        // map
        ignite.compute()
                .withExecutor(jobMeta.getId())
                .run(new IgniteRunnable() {

                    @IgniteInstanceResource
                    private Ignite igniteRemote;

                    @Override
                    public void run() {
                        final MapReduceJob<Serializable, Serializable> mapReduceJob = igniteRemote.services().serviceProxy(jobMeta.getId(), MapReduceJob.class, false);
                        final List<Serializable> items = mapReduceJob.mapper().map();
                        if (items != null) {
                            final List<IgniteFuture<Serializable>> igniteFutures = items.stream().parallel()
                                    .map(item ->
                                            igniteRemote.compute()
                                                    .callAsync(new IgniteCallable<Serializable>() {

                                                        @IgniteInstanceResource
                                                        private Ignite igniteRemoteAgain;

                                                        @Override
                                                        public Serializable call() {
                                                            final MapReduceJob<Serializable, Serializable> mapReduceJobInner = igniteRemote.services().serviceProxy(jobMeta.getId(), MapReduceJob.class, false);
                                                            return mapReduceJobInner.executor().execute(item);
                                                        }
                                                    })
                                    )
                                    .collect(Collectors.toList());


                            final List<Serializable> results = igniteFutures.stream().map(IgniteFuture::get).collect(Collectors.toList());

                            mapReduceJob.reducer().reduce(new ArrayList<>(results));
                        }
                    }
                });

    }

    @Override
    public Service asService(Object job) {
        return new MapReduceJobService((MapReduceJob) job);
    }

    class MapReduceJobService extends ServiceAdapter implements MapReduceJob<Serializable, Serializable> {

        private MapReduceJob<Serializable, Serializable> mapReduceJob;

        public MapReduceJobService(MapReduceJob<Serializable, Serializable> mapReduceJob) {
            this.mapReduceJob = mapReduceJob;
        }

        @Override
        public Mapper<Serializable> mapper() {
            return mapReduceJob.mapper();
        }

        @Override
        public Executor<Serializable, Serializable> executor() {
            return mapReduceJob.executor();
        }

        @Override
        public Reducer<Serializable> reducer() {
            return mapReduceJob.reducer();
        }
    }

}
