package com.example.onebatis.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Signature {

    /**
     * 执行器类型
     * @return  执行器
     */
    Class<?> type();

    /**
     * 被拦截执行器的方法
     * @return      可选： query/update
     */
    String method();

    /**
     *  Invocation 对象中 args参数类型
     * @return  {@code com.example.onebatis.plugin.Invocation}
     */
    Class<?>[] args();

}
