package com.example.onebatis;

import lombok.Getter;

/**
 * <p>
 *      连接对象
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 16:59
 **/
@Getter
public class DataSource {

    private String username;
    private String password;
    private String url;
    private String driver;
    private int poolSize;

    public static final String DEFAULT_POOL_SIZE = "1";


    public DataSource setUsername(String username) {
        this.username = username;
        return this;
    }

    public DataSource setPassword(String password) {
        this.password = password;
        return this;
    }

    public DataSource setUrl(String url) {
        this.url = url;
        return this;
    }

    public DataSource setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    public DataSource setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }
}
