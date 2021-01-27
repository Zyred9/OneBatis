package com.example.onebatis.pool;

import java.sql.Connection;
import java.util.ResourceBundle;

/**
 * <p>
 *          连接池
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public interface ConnectionPool {

    /**
     * 获取一个连接
     * @return      连接对象
     */
    Connection getConnection();

    /**
     * 关闭一个连接
     * @param conn      连接对象
     */
    void close(Connection conn);
}
