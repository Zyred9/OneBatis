package com.example.onebatis.session.defaults;

import com.example.onebatis.binding.MapperRegistry;
import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.executor.Executor;
import com.example.onebatis.session.Configuration;
import com.example.onebatis.session.SqlSession;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 *          默认的查询
 *          主要方法包括： getMapper() 根据mapper接口获取到动态代理后的mapper
 *                      selectList() 查询集合
 *                      selectOne()  查询单挑记录
 *                      flushStatement()  刷新statement
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 14:00
 **/
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;
    private Executor executor;
    private boolean autoCommit;

    public DefaultSqlSession(Configuration configuration, Executor ex, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = ex;
        this.autoCommit = autoCommit;
    }

    @Override
    public <T> T getMapper(Class<?> clazz) {
        MapperRegistry registry = this.configuration.getMapperRegistry();
        if (!registry.hasMapper(clazz)) {
            throw new RuntimeException("Mapper registry not found, case: " + clazz.getSimpleName());
        }
        return (T) registry.getMapper(clazz, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> List<T> selectList(SqlBuilder sqlBuilder, Object[] args, Class object) {
        return this.executor.query(args, sqlBuilder);
    }

    @Override
    public <T> T selectOne(SqlBuilder sqlBuilder, Object[] args, Class object) {
        List<T> list = this.selectList(sqlBuilder, args, object);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public int insert(Object[] args, SqlBuilder sqlBuilder) {
        return this.update(args, sqlBuilder);
    }

    @Override
    public int update(Object[] args, SqlBuilder sqlBuilder) {
        return this.executor.update(args, sqlBuilder);
    }

    @Override
    public int delete(Object[] args, SqlBuilder sqlBuilder) {
        return this.update(args, sqlBuilder);
    }

    @Override
    public Object flushStatements() {
        return Collections.EMPTY_LIST;
    }


}
