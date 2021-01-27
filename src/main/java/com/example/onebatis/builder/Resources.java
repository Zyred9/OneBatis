package com.example.onebatis.builder;

import cn.hutool.core.io.resource.ClassPathResource;

import java.io.*;
import java.net.URL;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 8:17
 **/
public class Resources {

    /**
     * 通过 hutool 工具读取文件
     * @param resource  文件路径
     * @return      stream
     */
    public static InputStream getResourceAsStream(String resource) {
        return new ClassPathResource(resource).getStream();
    }

}
