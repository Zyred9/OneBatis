package com.example.onebatis.handler;


import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.parsing.DynamicSqlNode;
import com.example.onebatis.session.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 *      查询预处理
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/17 16:48
 **/
public class StatementHandler {

    /** 结果预处理 **/
    private final ResultSetHandler resultSetHandler = new ResultSetHandler();

    /**
     * 这里才是真正的JDBC操作，真正的查询
     * @param parameters        sql参数补充
     * @param sqlBuilder        sql
     * @param configuration     全局配置文件
     * @param autoCommit        是否自动提交
     * @param <T>               查询结果泛型
     * @return                  查询结果
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Object[] parameters, SqlBuilder sqlBuilder, Configuration configuration, boolean autoCommit) {
        Connection connection = null;
        try {
            // 从连接池中获取数据库连接
            connection = configuration.getConnectionPool().getConnection();
            // 拿到查询预处理，进行sql参数封装等等..
            PreparedStatement statement = getStatement(connection, sqlBuilder, parameters);
            // 执行sql
            statement.execute();
            // 拿到执行结果类型
            String resultType = sqlBuilder.getResultType();
            // 使用反射生成一个返回结果类型
            Class<?> pojo = Class.forName(resultType);
            // 结果集处理器返回结果
            return (List<T>) this.resultSetHandler.handlerResult(pojo, statement.getResultSet());
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            // 归还连接池
            configuration.getConnectionPool().close(connection);
        }
        return null;
    }

    /**
     * 更新操作
     * @param args          sql参数
     * @param sqlBuilder    sql
     * @param configuration 全局配置文件
     * @return              操作成功条数
     */
    public int update(Object[] args, SqlBuilder sqlBuilder, Configuration configuration) {
        Connection connection = null;
        try {
            // 获取连接
            connection = configuration.getConnectionPool().getConnection();
            // 操作预处理
            PreparedStatement statement = getStatement(connection, sqlBuilder, args);
            statement.execute();
            // 直接返回操作结果
            return statement.getUpdateCount();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // 归还连接池
            configuration.getConnectionPool().close(connection);
        }
        return 0;
    }

    /**
     * 进行sql动态拼接
     * @param connection        数据库连接
     * @param sqlBuilder        sql
     * @param parameters        参数
     * @return                  预处理对象
     * @throws SQLException     sql执行异常
     */
    private PreparedStatement getStatement(Connection connection, SqlBuilder sqlBuilder, Object[] parameters) throws SQLException {

        // 这里需要对sql处理 占位符 #{} 变成 ？
        DynamicSqlNode node = new DynamicSqlNode(sqlBuilder.getSql(), parameters);
        // 重新赋值sql
        sqlBuilder.setSql(node.parsePlaceholder());
        // PreparedStatement 是通过 connection 获取的
        PreparedStatement statement = connection.prepareStatement(sqlBuilder.getSql());
        // 参数处理器
        ParameterHandler parameterHandler = new ParameterHandler(statement);
        parameterHandler.setParameters(parameters, node.mapping);
        return statement;
    }
}
