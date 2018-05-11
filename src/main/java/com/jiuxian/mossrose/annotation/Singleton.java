package com.jiuxian.mossrose.annotation;

import java.lang.annotation.*;

/**
 * 每JVM中只加载一份此job的实例
 * <p>
 * 仅在<code>ClassnameObjectResource</code>模式下有效
 * <p>
 * Job生成的内部类不会是单例的，每次都生成一个干净的内部类是符合简单性原则的，如果有比较重的初始化工作，请放在Job外部类中
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Singleton {

}
