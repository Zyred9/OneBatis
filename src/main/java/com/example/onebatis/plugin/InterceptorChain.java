package com.example.onebatis.plugin;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 *      拦截器链
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class InterceptorChain {

    private static final List<Interceptor> chain = new LinkedList<>();

    /**
     *  为被拦截的执行器，创建一个代理对象，其中被拦截的方法(query/update)，被执行，那么就执行动态代理的 invoke()
     * @param target        被代理的执行器
     */
    public Object pluginAll(Object target) {
        for (Interceptor interceptor : chain) {
            target = interceptor.plugin(target);
        }
        return target;
    }


    public void addInterceptor(Interceptor interceptor) {
        chain.add(interceptor);
    }


    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(chain);
    }

}
