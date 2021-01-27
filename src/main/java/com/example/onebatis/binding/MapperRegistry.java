package com.example.onebatis.binding;

import com.example.onebatis.session.Configuration;
import com.example.onebatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 16:54
 **/
public class MapperRegistry {

    private Configuration config;
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration config) {
        this.config = config;
    }

    public boolean hasMapper(Class<?> type) {
        return knownMappers.containsKey(type);
    }

    /**
     * 注册mapper 到 mapper容器中
     *
     * @param type
     * @param <T>
     */
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                knownMappers.put(type, new MapperProxyFactory<>(type));
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }


    /**
     * 从初始化完毕的mapper接口对应的 xml 内容获取到mybatis对每个接口对应的 MapperProxyFacotry，生成对应的代理类，
     *
     * @param type
     * @param sqlSession
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        // 从初始化阶段完成的mapper接口注册中获取到当前接口对应的代理类
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

}
