package com.example.onebatis.parsing;

import com.example.onebatis.DataSource;
import com.example.onebatis.interceptor.Interceptor;
import com.example.onebatis.pool.ConnectionPool;
import com.example.onebatis.pool.DefaultConnectionPool;
import com.example.onebatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * <p>
 * xml 解析器
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 15:27
 **/
public class XmlParser {

    private final InputStream inputStream;
    private final Configuration configuration;

    public XmlParser(InputStream inputStream, Configuration configuration) {
        this.inputStream = inputStream;
        this.configuration = configuration;
    }

    /**
     *  开始解析xml配置文件
     */
    public void parse() {
        try {
            // 使用SAX解析xml
            SAXReader sax = new SAXReader();
            Document document = sax.read(this.inputStream);
            // 获取到root节点
            Element root = document.getRootElement();
            List<Element> elements = root.elements();

            for (Element element : elements) {
                if (element == null) {
                    continue;
                }
                switch (element.getName()) {
                    case "properties":
                        parseProperties(element);
                        break;
                    case "settings":
                        parseSettings(element);
                        break;
                    case "plugins":
                        parsePlugins(element);
                        break;
                    case "environments":
                        parseEnvironments(element);
                        break;
                    case "mappers":
                        parseMappers(element);
                        break;
                    case "typeAliases":
                        this.parseTypeAliases(element);
                        break;
                    default:
                        break;
                }

                // 初始化连接池
                ConnectionPool pool = DefaultConnectionPool.getInstance(configuration);
                this.configuration.setConnectionPool(pool);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parseTypeAliases(Element element) {
    }

    /**
     * 解析 <mappers>节点，就是配置的 xxMapper.xml
     * @param mapper        mapper节点信息
     */
    private void parseMappers(Element mapper) {
        List<Element> elements = mapper.elements();
        List<String> arrResource = new ArrayList<>();
        // 扫描所有的节点，将其放入集合中，用于下一步真正解析sql语句做准备
        for (Element element : elements) {
            arrResource.add(element.attribute("resource").getValue());
        }
        // 解析 mapper.xml 文件，读取内部的sql
        new MapperParser(arrResource, this.configuration).parse();
    }

    /**
     * 解析环境变量配置节点，这里您可能会担心先读取了environments节点，
     * 然后再读取 properties节点，这样的情况不会出现的，因为mybatis的
     * dtd已经做了约束的
     * @param element   节点信息
     */
    private void parseEnvironments(Element element) {
        // 获取第二层节点集合
        List<Element> environments = element.elements();
        for (Element environment : environments) {

            // 获取第三层节点信息
            List<Element> elements = environment.elements();
            for (Element sunElement : elements) {
                // 接下来是读取配置文件，匹配dataSource节点
                if (Objects.equals(sunElement.getName(), "dataSource")) {
                    DataSource dataSource = new DataSource();
                    // 读取 dataSource 内部的配置
                    List<Element> properties = sunElement.elements();
                    for (Element property : properties) {
                        // 这里是获取到配置在 <properties/> 节点中被读取的内容，被放入到了ResourceBundle内，现在通过这个对象取值
                        ResourceBundle bundle = this.configuration.getProperties();
                        switch (property.attribute("name").getValue()) {
                            case "username":
                                // 注入数据库连接用户名
                                dataSource.setUsername(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            case "driver":
                                // 注入数据库连接驱动
                                dataSource.setDriver(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            case "url":
                                // 注入数据库连接地址
                                dataSource.setUrl(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            case "password":
                                // 注入数据库连接密码
                                dataSource.setPassword(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            default:
                                break;
                        }
                    }

                    // 最后将读取到的内容放入全局配置文件中
                    this.configuration.setDataSource(dataSource);
                }
            }
        }
    }

    /**
     * 解析 plugins 节点
     * @param element       节点信息
     */
    private void parsePlugins(Element element) {
        List<Element> plugins = element.elements();
        try {
            for (Element plugin : plugins) {
                // 获取拦截器配置节点
                String interceptor = plugin.attribute("interceptor").getValue();
                // 针对拦截器配置的类实例化，并注入到全局配置文件中
                Class<?> clazz = Class.forName(interceptor);
                Interceptor instance = (Interceptor) clazz.newInstance();
                this.configuration.addInterceptor(instance);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 解析 <properties> 节点
     * @param element   节点信息
     */
    private void parseProperties(Element element) {
        String resourcePath = element.attribute("resource").getValue();
        int index = resourcePath.lastIndexOf(".");
        String fileName = resourcePath.substring(0, index);
        // 拿到文件名称，通过ResourceBundle工具读取，并放入全局配置文件中
        this.configuration.setProperties(ResourceBundle.getBundle(fileName));
    }

    /**
     * 解析settings节点， settings节点主要存放mybatis的一些开关项
     * @param element       节点信息
     */
    private void parseSettings(Element element) {
        // 拿到所有的子节点
        List<Element> settings = element.elements();
        try {
            // 遍历子节点
            for (Element setting : settings) {
                // 分别取 name value 属性
                String name = setting.attribute("name").getValue();
                String value = setting.attribute("value").getValue();
                Field field = configuration.getClass().getDeclaredField(name);
                field.setAccessible(true);
                // 使用反射装载到 全局配置文件中
                field.set(configuration, value);
                field.setAccessible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  ${} 针对这个标签进行解析，如 ${jdbc.driver}
     * @param str       ${jdbc.driver}
     * @return          jdbc.driver
     */
    private String sub(String str){
        if (str.contains("${")){
            str = str.replaceAll("\\$\\{|}", "");
        }
        return str;
    }
}
