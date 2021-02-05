package com.example.onebatis.plugin;

import cn.hutool.core.collection.CollectionUtil;
import com.example.onebatis.plugin.annotations.Interceptors;
import com.example.onebatis.plugin.annotations.Signature;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * <p>
 * 插件类，需要实现 JDK 动态代理的 InvocationHandler 接口，
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class Plugin implements InvocationHandler, Serializable {

    /**
     * 被代理的目标对象
     **/
    private final Object target;
    /**
     * 实现Interceptor接口的子类
     **/
    private final Interceptor interceptor;
    /**
     * Interceptor实现类上的注解@Signature的参数
     **/
    private final Map<Class<?>, Set<Method>> signatureMap;

    private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    /**
     * 生成目标对象的动态代理对象
     * @param target        目标
     * @param interceptor   实现的接口，使用JDK动态代理
     * @return              proxy 后的对象
     */
    public static Object wrap(Object target, Interceptor interceptor) {
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);

        Class<?> targetClazz = target.getClass();
        Class<?>[] allInterfaces = getAllInterfaces(targetClazz, signatureMap);

        if (allInterfaces.length == 0) {
            return target;
        }
        // 直接通过动态代理完成对目标的代理，这里的 h 参数是 new Plugin(),
        // 那么发生了动态代理的时候，调用的也是Plugin#invock()
        return Proxy.newProxyInstance(targetClazz.getClassLoader(),
                allInterfaces, new Plugin(target, interceptor, signatureMap));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodClazz = method.getDeclaringClass();
        Set<Method> methods = this.signatureMap.get(methodClazz);

        if (CollectionUtil.isNotEmpty(methods) && methods.contains(method)) {
            // 这里直接调用实现类的 interceptor 方法进行执行
            return interceptor.intercept(new Invocation(this.target, method, args));
        }

        // 否则执行源对象的方法
        return method.invoke(target, args);
    }


    /**
     * 获取Interceptor实现类上 @Interceptor(@Signature({})) 内的参数
     *
     * @param interceptor 实现类
     * @return Class<? extends Executors.class> -> Method
     */
    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
        // 获取实现类上的注解
        Interceptors interceptors = interceptor.getClass().getAnnotation(Interceptors.class);
        if (interceptors == null) {
            throw new RuntimeException("Annotation @Interceptors undefined, Cause :" + interceptor.getClass().getName());
        }
        // 获取到内部所有的子注解，@Signature
        Signature[] signatures = interceptors.value();

        Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();

        for (Signature sign : signatures) {

            // 这里是将sign 的type属性作为map的key，然后创建val为 new HashSet(), 并且返回 val
            Set<Method> methods = signatureMap.computeIfAbsent(sign.type(), k -> new HashSet<>());
            try {
                // 通过反射，对 @Signature 内部属性 method 和 args 属性拿到，并生成一个与之对应的方法
                Method method = sign.type().getMethod(sign.method(), sign.args());
                methods.add(method);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Could not find method on " + sign.type() + " named " + sign.method() + ". Cause: " + ex, ex);
            }
        }
        return signatureMap;
    }


    /**
     * 将所有包含在 map 中的类全部扫描到结果中
     * @param targetClazz       目标类的Class对象
     * @param map               方法的签名
     * @return                  所有的实现接口
     */
    private static Class<?>[] getAllInterfaces(Class<?> targetClazz, Map<Class<?>, Set<Method>> map) {
        Set<Class<?>> interfaces = new HashSet<>();
        while (targetClazz != null) {
            for (Class<?> clazz : targetClazz.getInterfaces()) {
                if (map.containsKey(clazz)) {
                    interfaces.add(clazz);
                }
            }
            targetClazz = targetClazz.getSuperclass();
        }
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }
}
