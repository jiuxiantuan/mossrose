package com.jiuxian.mossrose.compute;

import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAdapter implements Service {


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAdapter.class);

    @Override
    public void cancel(ServiceContext ctx) {
        // NO OP
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        LOGGER.info("Service init {}", ctx.name());
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        // NO OP
    }

}
