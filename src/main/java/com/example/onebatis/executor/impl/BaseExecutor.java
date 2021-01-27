package com.example.onebatis.executor.impl;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.executor.Executor;
import com.example.onebatis.handler.StatementHandler;
import com.example.onebatis.session.Configuration;

import java.util.List;

/**
 * <p>
 *          提供多种执行器，其中最基础的执行器满足 crud 操作
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class BaseExecutor implements Executor {

    /** 全局配置文件 **/
    protected final Configuration configuration;
    /** 是否自动提交事务 **/
    protected final boolean autoCommit;

    public BaseExecutor(Configuration configuration, boolean autoCommit) {
        this.configuration = configuration;
        this.autoCommit = autoCommit;
    }

    @Override
    public <T> List<T> query(Object[] parameters, SqlBuilder sqlBuilder) {
        // 执行器执行，其实是交给了 StatementHandler 执行，
        // StatementHandler主要作用是对 Statement进行处理，那么 Statement属于JDBC层面
        StatementHandler statementHandler = new StatementHandler();
        return statementHandler.query(parameters, sqlBuilder, this.configuration, this.autoCommit);
    }

    @Override
    public int update(Object[] parameters, SqlBuilder sqlBuilder) {
        // 这里同上
        StatementHandler statementHandler = new StatementHandler();
        return statementHandler.update(parameters, sqlBuilder, this.configuration);
    }


}
