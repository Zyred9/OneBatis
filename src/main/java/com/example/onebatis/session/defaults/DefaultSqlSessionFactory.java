package com.example.onebatis.session.defaults;

import com.example.onebatis.executor.Executor;
import com.example.onebatis.session.Configuration;
import com.example.onebatis.session.SqlSession;
import com.example.onebatis.session.SqlSessionFactory;

/**
 * <p>
 *      默认的sqlSession会话
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 14:03
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    /**
     * 引入全局配置
     */
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        // 开启一个 sqlSession，参数 autoCommit 是是否自动提交事务
        return openSessionFromDataSource(false);
    }

    /**
     * 从全局看来，此时的 configuration 已经初始化完毕了，那么我需要获取一个执行器来执行sql了
     *
     * 那么问题来了，一个sql执行器跟一个session是什么关系：等价关系，绑定关系
     *
     * @param autoCommit    是否自动提交
     * @return              sql会话
     */
    private SqlSession openSessionFromDataSource(boolean autoCommit){
        // 从内部获取一个执行器
        Executor ex = configuration.newExecutor(autoCommit);
        // 创建一个会话，将执行器绑定到会话中
        return new DefaultSqlSession(configuration, ex, autoCommit);
    }
}
