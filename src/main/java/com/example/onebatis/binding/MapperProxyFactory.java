package com.example.onebatis.binding;

import com.example.onebatis.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * <>
 * 代理Mapper接口类的工厂
 * </>
 *
 * @param <T>
 * @author zyred
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

//    public Map<Method, MapperMethod> getMethodCache() {
//        return methodCache;
//    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
    }

    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return newInstance(mapperProxy);
    }

}
