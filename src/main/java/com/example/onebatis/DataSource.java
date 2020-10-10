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


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
