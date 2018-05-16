package com.jiuxian.mossrose.compute;

import org.apache.ignite.services.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class JobServiceProxy implements InvocationHandler {

    private Object job;

    private final ServiceAdapter serviceAdapter = new ServiceAdapter();

    private static final List<Method> SERVICE_METHODS = Arrays.asList(Service.class.getMethods());

    public JobServiceProxy(Object job) {
        this.job = job;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (SERVICE_METHODS.contains(method)) {
            return method.invoke(serviceAdapter, args);
        }
        return method.invoke(job, args);
    }
}
