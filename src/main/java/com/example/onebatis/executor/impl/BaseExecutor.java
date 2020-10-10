package com.example.onebatis.executor.impl;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.executor.Executor;
import com.example.onebatis.handler.StatementHandler;
import com.example.onebatis.session.Configuration;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class BaseExecutor implements Executor {

    protected Configuration configuration;
    protected boolean autoCommit;

    public BaseExecutor(Configuration configuration, boolean autoCommit) {
        this.configuration = configuration;
        this.autoCommit = autoCommit;
    }

    @Override
    public <T> List<T> query(Object[] parameters, SqlBuilder sqlBuilder) {
        StatementHandler statementHandler = new StatementHandler();
        return statementHandler.query(parameters, sqlBuilder, this.configuration, this.autoCommit);
    }

    @Override
    public int update(Object[] parameters, SqlBuilder sqlBuilder) {
        StatementHandler statementHandler = new StatementHandler();
        return statementHandler.update(parameters, sqlBuilder, this.configuration);
    }


}
