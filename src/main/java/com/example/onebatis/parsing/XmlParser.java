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

    private InputStream inputStream;
    private Configuration configuration;

    public XmlParser(InputStream inputStream, Configuration configuration) {
        this.inputStream = inputStream;
        this.configuration = configuration;
    }

    public Configuration parse() {
        try {
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
        return this.configuration;
    }

    private void parseTypeAliases(Element element) {
    }

    private void parseMappers(Element mapper) {
        List<Element> elements = mapper.elements();
        List<String> arrResource = new ArrayList<>();
        for (Element element : elements) {
            arrResource.add(element.attribute("resource").getValue());
        }
        // 解析 mapper
        new MapperParser(arrResource, this.configuration).parse();
    }

    private void parseEnvironments(Element element) {
        List<Element> environments = element.elements();
        for (Element environment : environments) {

            List<Element> elements = environment.elements();
            for (Element sunElement : elements) {
                if (Objects.equals(sunElement.getName(), "dataSource")) {
                    DataSource dataSource = new DataSource();
                    List<Element> properties = sunElement.elements();
                    for (Element property : properties) {
                        ResourceBundle bundle = this.configuration.getProperties();
                        switch (property.attribute("name").getValue()) {
                            case "username":
                                dataSource.setUsername(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            case "driver":
                                dataSource.setDriver(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            case "url":
                                dataSource.setUrl(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            case "password":
                                dataSource.setPassword(bundle.getString(sub(property.attribute("value").getValue())));
                                break;
                            default:
                                break;
                        }
                    }
                    this.configuration.setDataSource(dataSource);
                }
            }
        }
    }

    private void parsePlugins(Element element) {
        List<Element> plugins = element.elements();
        try {
            for (Element plugin : plugins) {
                String interceptor = plugin.attribute("interceptor").getValue();
                Class<?> clazz = Class.forName(interceptor);
                Interceptor instance = (Interceptor) clazz.newInstance();
                this.configuration.addInterceptor(instance);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parseProperties(Element element) {
        String resourcePath = element.attribute("resource").getValue();
        int index = resourcePath.lastIndexOf(".");
        String fileName = resourcePath.substring(0, index);
        this.configuration.setProperties(ResourceBundle.getBundle(fileName));
    }

    private void parseSettings(Element element) {
        List<Element> settings = element.elements();
        try {
            for (Element setting : settings) {
                String name = setting.attribute("name").getValue();
                String value = setting.attribute("value").getValue();
                Field field = configuration.getClass().getDeclaredField(name);
                field.setAccessible(true);
                field.set(configuration, value);
                field.setAccessible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String sub(String str){
        if (str.contains("${")){
            str = str.replaceAll("\\$\\{|}", "");
        }
        return str;
    }
}
