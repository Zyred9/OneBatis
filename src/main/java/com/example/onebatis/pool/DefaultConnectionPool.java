package com.example.onebatis.pool;

import com.example.onebatis.DataSource;
import com.example.onebatis.session.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 连接池，单例
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/17 16:58
 **/
public class DefaultConnectionPool implements ConnectionPool {

    /** 连接池 **/
    private static LinkedList<Connection> pool;
    private static DefaultConnectionPool connectionPool;
    private final Configuration configuration;

    private DefaultConnectionPool() {
        throw new RuntimeException("Singleton pool can not be Constructor create instance.");
    }

    private DefaultConnectionPool(Configuration configuration) {
        if (configuration.getConnectionPool() != null) {
            throw new RuntimeException("Singleton pool is exist!");
        }
        this.configuration = configuration;
        init();
    }

    /**
     * 采用双重检查锁创建单例
     * @param configuration     全局配置
     * @return                  连接池
     */
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

    /**
     * 读取数据库配置文件，初始化dataSource对象
     * @param properties        数据库配置文件
     * @return                    DataSource
     */
    public DataSource createConnection(ResourceBundle properties) {
        String driverClassName = getProperties(properties, "jdbc.driver");
        String userName = getProperties(properties,"jdbc.username");
        String url = getProperties(properties,"jdbc.url");
        String password = getProperties(properties,"jdbc.password");
        String size = properties.getString("connection.size");
        int connectionSize = Integer.parseInt(size.equals("") ? DataSource.DEFAULT_POOL_SIZE : size);
        try {
            // 加载连接驱动
            Class.forName(driverClassName);
            return new DataSource()
                    .setDriver(driverClassName)
                    .setPassword(password)
                    .setUrl(url)
                    .setUsername(userName)
                    .setPoolSize(connectionSize);
        }catch (ClassNotFoundException ex){
            throw new RuntimeException("Loading driver failure, case: " + ex);
        }
    }

    private String getProperties(ResourceBundle properties, String name) {
        return properties.getString(name);
    }


    private void init(){
        // 初始化连接池大小
        DataSource s = createConnection(this.configuration.getProperties());
        this.configuration.setDataSource(s);
        pool = new LinkedList<>();
        try {
            for (int i = 0; i <= s.getPoolSize(); i++) {
                pool.add(DriverManager.getConnection(s.getUrl(), s.getDriver(), s.getPassword()));
            }
        }catch (SQLException ex) {
            throw new RuntimeException("Initialize the connection pool failure, case: " + ex);
        }
    }
}
