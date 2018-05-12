package com.jiuxian.mossrose.job.to;

import com.jiuxian.mossrose.compute.GridComputer;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于缓存对象的容器，避免Ignite远程计算时反复创建对象
 */
public final class ObjectContainer {

    private static final String GRID_COMPUTER = "gridComputer";
    private static Map<String, Object> objects = new HashMap<>();

    public static void put(String key, Object object) {
        objects.put(key, object);
    }

    public static <T> T get(String key) {
        return (T) objects.get(key);
    }

    public static Class<?> getClazz(String key) {
        return objects.get(key).getClass();
    }

    public static void putGridComputer(GridComputer gridComputer) {
        put(GRID_COMPUTER, gridComputer);
    }

    public static GridComputer getGridComputer() {
        return get(GRID_COMPUTER);
    }

}
