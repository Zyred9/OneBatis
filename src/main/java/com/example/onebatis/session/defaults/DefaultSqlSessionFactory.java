package com.example.onebatis.session.defaults;

import com.example.onebatis.executor.Executor;
import com.example.onebatis.session.Configuration;
import com.example.onebatis.session.SqlSession;
import com.example.onebatis.session.SqlSessionFactory;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 14:03
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(false);
    }


    private SqlSession openSessionFromDataSource(boolean autoCommit){
        Executor ex = configuration.newExecutor(autoCommit);
        return new DefaultSqlSession(configuration, ex, autoCommit);
    }
}
