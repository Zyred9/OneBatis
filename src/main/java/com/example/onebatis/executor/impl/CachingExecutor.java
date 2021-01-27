package com.example.onebatis.executor.impl;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.cache.CacheKey;
import com.example.onebatis.executor.Executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class CachingExecutor implements Executor {


    private final Executor delegate;

    /**
     * 查询缓存
     **/
    private static final Map<Integer, Object> cache = new HashMap<>();

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Object[] parameters, SqlBuilder sqlBuilder) {
        Integer cacheKey = createCacheKey(parameters, sqlBuilder);
        // 命中缓存
        if (cache.containsKey(cacheKey)) {
            System.out.println("Hit the cache.");
            return (List<T>) cache.get(cacheKey);
        }
        // 走数据库查询
        System.out.println("Database query.");
        Object result = delegate.query(parameters, sqlBuilder);
        cache.put(cacheKey, result);
        return (List<T>) result;
    }

    private Integer createCacheKey(Object[] parameters, SqlBuilder sqlBuilder) {
        CacheKey cacheKey = new CacheKey();
        cacheKey.updateAll(parameters);
        cacheKey.update(sqlBuilder);
        return cacheKey.hashCode();
    }

    @Override
    public int update(Object[] parameters, SqlBuilder sqlBuilder) {
        return 0;
    }



}
