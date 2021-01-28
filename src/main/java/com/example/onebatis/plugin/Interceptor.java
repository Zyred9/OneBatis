package com.example.onebatis.plugin;

import java.util.Properties;

/**
 * <p>
 *      拦截器，所有的自定义拦截器都需要实现该接口，并实现 interceptor 方法
 * </p>
 *
 * @author zyred
 * @createTime 2021/1/27 15:42
 **/
public interface Interceptor {

    /**
     * 处理拦截的方法
     * @param invocation        从注解中获取的执行参数
     * @return                  执行后返回的结果
     * @throws Throwable        异常
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 插件方法，该方法有 chain 掉用
     * @param target    被代理的目标 Executor 执行器
     * @return          目标类的代理对象
     */
    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    default void setProperties(Properties properties) {}

}
