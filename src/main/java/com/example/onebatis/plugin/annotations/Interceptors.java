package com.example.onebatis.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *      拦截器注解
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptors {

    /**
     * 主要是为了让 Mybatis 知道应该拦截调执行器的哪一个方法，是 update 还是 query
     * @return      方法签名
     */
    Signature[] value();
}
