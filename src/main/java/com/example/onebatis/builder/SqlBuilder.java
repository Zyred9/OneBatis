package com.example.onebatis.builder;


import com.example.onebatis.mapping.SqlCommandType;
import com.example.onebatis.session.Configuration;
import lombok.Getter;

/**
 * <p>
 *      每个 select update deleted insert 标签中，存储对应的属性
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 14:59
 **/
@Getter
public class SqlBuilder {

    /** 方法名称 **/
    private String id;

    /** 这里不能存被new出来的对象，而是用的时候才去，否则会线程不安全 **/
    private String resultType;

    /** 参数类型 **/
    private String parameterType;

    private boolean flushCache = false;

    private boolean useCache = false;

    private SqlCommandType commandType;

    private String sql ;

    private String statementId;

    private Configuration configuration;

    public SqlBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public SqlBuilder setResultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public SqlBuilder setParameterType(String parameterType) {
        this.parameterType = parameterType;
        return this;
    }

    public SqlBuilder setFlushCache(boolean flushCache) {
        this.flushCache = flushCache;
        return this;
    }

    public SqlBuilder setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    public SqlBuilder setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public SqlBuilder setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public SqlBuilder setStatementId(String statementId) {
        this.statementId = statementId;
        return this;
    }

    public SqlBuilder setCommandType(SqlCommandType commandType) {
        this.commandType = commandType;
        return this;
    }

}
