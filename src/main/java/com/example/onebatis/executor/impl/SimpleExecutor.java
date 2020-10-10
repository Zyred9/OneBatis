package com.example.onebatis.executor.impl;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.handler.StatementHandler;
import com.example.onebatis.session.Configuration;

import java.util.List;

/**
 * <p>
 *          简单执行器
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class SimpleExecutor extends BaseExecutor {


    public SimpleExecutor(Configuration configuration, boolean autoCommit) {
        super(configuration, autoCommit);
    }

    @Override
    public <T> List<T> query(Object[] parameters, SqlBuilder sqlBuilder) {
        return super.query(parameters, sqlBuilder);
    }

    @Override
    public int update(Object[] parameters, SqlBuilder sqlBuilder) {
        return super.update(parameters, sqlBuilder);
    }

}
