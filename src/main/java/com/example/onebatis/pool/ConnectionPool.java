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

    Connection getConnection();

    void close(Connection conn);

    Connection createConnection(ResourceBundle properties) throws ClassNotFoundException;
}
