package com.example.onebatis.builder;

import com.example.onebatis.parsing.XmlParser;
import com.example.onebatis.session.Configuration;

import java.io.InputStream;

/**
 * <p>
 * 解析XML
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 14:09
 **/
public class XmlConfigBuilder {

    private Configuration configuration;

    public XmlConfigBuilder(InputStream inputStream) {
        configuration = new Configuration();
        new XmlParser(inputStream, configuration).parse();
    }

    public Configuration parse() {
        return configuration;
    }


}
