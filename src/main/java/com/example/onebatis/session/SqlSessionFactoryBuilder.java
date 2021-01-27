package com.example.onebatis.session;

import com.example.onebatis.builder.XmlConfigBuilder;
import com.example.onebatis.session.defaults.DefaultSqlSessionFactory;

import java.io.InputStream;

/**
 * <p>
 *      用于创建SqlSessionFactory
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 14:05
 **/
public class  SqlSessionFactoryBuilder {

    /**
     * 这里用于解析，初始化整个FastBatis上下文
     * @param inputStream       读取配置文件的输入流
     * @return      解析完毕后，生成的sqlSessionFactory, 用于创建sqlSession
     */
    public SqlSessionFactory build(InputStream inputStream){
        XmlConfigBuilder builder = new XmlConfigBuilder(inputStream);
        return build(builder.parse());
    }


    /**
     * 构建一个sqlSessionFactory对象
     * @param config        全局配置文件
     * @return              sqlSessionFactory
     */
    private SqlSessionFactory build(Configuration config) {
        // 全部创建完毕后，直接 new 出默认的session工厂
        return new DefaultSqlSessionFactory(config);
    }
}
