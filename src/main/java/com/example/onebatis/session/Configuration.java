package com.example.onebatis.session;

import com.example.onebatis.DataSource;
import com.example.onebatis.binding.MapperRegistry;
import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.executor.Executor;
import com.example.onebatis.executor.impl.CachingExecutor;
import com.example.onebatis.executor.impl.SimpleExecutor;
import com.example.onebatis.interceptor.Interceptor;
import com.example.onebatis.pool.ConnectionPool;

import java.util.*;

/**
 * <p>
 * 总配置文件
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 13:54
 **/
public final class Configuration {

    /** 全局缓存开关 **/
    private String cacheEnabled;
    /** 日志实现类 **/
    private String logImpl;
    /** 懒加载 **/
    private String lazyLoadingEnabled;
    /** 拦截器，暂时未实现 **/
    private final List<Interceptor> interceptors = new ArrayList<>();
    /** 连接信息 **/
    private DataSource dataSource;
    /** 其实这里面就包含了连接信息 **/
    private ResourceBundle properties;
    /** 数据库连接池 **/
    private ConnectionPool connectionPool;

    /** statementId -> sql **/
    private final HashMap<String, SqlBuilder> sqlMapping = new HashMap<>();
    /** Mapper注册 **/
    private final MapperRegistry mapperRegistry = new MapperRegistry(this);

    /** sql拦截器，主要是插件才使用 **/
    public void addInterceptor(Interceptor i) {
        interceptors.add(i);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setProperties(ResourceBundle properties) {
        this.properties = properties;
    }

    public ResourceBundle getProperties() {
        return this.properties;
    }

    public void addSqlMapping(String statementId, SqlBuilder sql) {
        this.sqlMapping.put(statementId, sql);
    }

    public void addMapper(Class<?> clazz) {
        this.mapperRegistry.addMapper(clazz);
    }

    public boolean hasMapper(Class<?> clazz){
        return this.mapperRegistry.hasMapper(clazz);
    }

    public boolean hasStatement(String statementId){
        return this.sqlMapping.containsKey(statementId);
    }

    public SqlBuilder getMappedStatement(String statementId) {
        return sqlMapping.get(statementId);
    }

    /**
     * 初始化执行器和插件注入
     * @param autoCommit  自动注入
     * @return            sql执行器
     */
    public Executor newExecutor(boolean autoCommit) {
        // 简单sql执行器
        Executor ex = new SimpleExecutor(this, autoCommit);
        if (Objects.deepEquals(cacheEnabled, "true")){
            // 这里使用了装饰器模式，来增强执行器对象
            ex = new CachingExecutor(ex);
        }
        // 先放弃插件的注入，下一个版本再加入
        // ex = (Executor) interceptorChain.pluginAll(ex);
        return ex;
    }

    public void setConnectionPool(ConnectionPool conn){
        this.connectionPool = conn;
    }

    public ConnectionPool getConnectionPool(){
        return connectionPool;
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

}
