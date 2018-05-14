package com.jiuxian.mossrose.job.to;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于缓存对象的容器，避免Ignite远程计算时反复创建对象
 */
public final class ObjectContainer {

    private static Map<String, Object> objects = new ConcurrentHashMap<>();

    public static void put(String key, Object object) {
        objects.put(key, object);
    }

    public static <T> T get(String key) {
        return (T) objects.get(key);
    }

    public static Class<?> getClazz(String key) {
        return objects.get(key).getClass();
    }
}
