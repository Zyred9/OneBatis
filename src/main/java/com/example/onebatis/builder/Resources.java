package com.example.onebatis.builder;

import cn.hutool.core.io.resource.ClassPathResource;

import java.io.InputStream;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 8:17
 **/
public class Resources {

    public static InputStream getResourceAsStream(String resource){

        return new ClassPathResource(resource).getStream();
    }

}
