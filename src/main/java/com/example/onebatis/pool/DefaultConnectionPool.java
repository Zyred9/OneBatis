package com.example.onebatis.pool;

import com.example.onebatis.session.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 连接池
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/17 16:58
 **/
public class DefaultConnectionPool implements ConnectionPool {

    private static LinkedList<Connection> pool;
    private static DefaultConnectionPool connectionPool;
    private static Configuration configuration;

    private DefaultConnectionPool() {
        throw new RuntimeException("Singleton pool can not be Constructor create instance.");
    }

    private DefaultConnectionPool(Configuration configuration) {
        if (configuration.getConnectionPool() != null) {
            throw new RuntimeException("Singleton pool is exist!");
        }
        init(this.configuration = configuration);
    }

    public static ConnectionPool getInstance(Configuration configuration){
        if (null == connectionPool){
            synchronized (DefaultConnectionPool.class){
                if (null == connectionPool){
                    connectionPool = new DefaultConnectionPool(configuration);
                }
            }
        }
        return connectionPool;
    }

    @Override
    public synchronized Connection getConnection() {
        Connection first = pool.getFirst();
        try {
            while (pool.getFirst() == null) {
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        pool.remove(first);
        return first;
    }

    @Override
    public synchronized void close(Connection conn) {
        pool.addLast(conn);
    }

    @Override
    public Connection createConnection(ResourceBundle properties) {
        String driverClassName = getProperties(properties, "jdbc.driver");
        String userName = getProperties(properties,"jdbc.username");
        String url = getProperties(properties,"jdbc.url");
        String password = getProperties(properties,"jdbc.password");
        try {
            Class.forName(driverClassName);
            return DriverManager.getConnection(url, userName, password);
        }catch (ClassNotFoundException | SQLException ex){
            throw new RuntimeException("Initialize the connection pool failure, case: " + ex);
        }
    }

    private String getProperties(ResourceBundle properties, String name) {
        return properties.getString(name);
    }


    private void init(Configuration configuration){
        // 初始化连接池大小
        ResourceBundle properties = configuration.getProperties();
        String size = properties.getString("connection.size");
        size = size == null || size == "" ? "2" : size;
        Integer connectionSize = Integer.parseInt(size);
        pool = new LinkedList<>();
        for (Integer i = 0; i <= connectionSize; i++) {
            pool.add(createConnection(properties));
        }
    }
}
