package com.example.onebatis.parsing;

import com.example.onebatis.builder.Resources;
import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.mapping.SqlCommandType;
import com.example.onebatis.session.Configuration;
import com.example.onebatis.util.ParseUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultText;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  mapper.xml 解析器
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 8:34
 **/
public class MapperParser {

    private final List<String> resources;
    private final Configuration configuration;

    public MapperParser(List<String> resources, Configuration configuration) {
        this.resources = resources;
        this.configuration = configuration;
    }

    /**
     * 准备解析 mapper.xml
     */
    public void parse() {
        for (String resource : resources) {
            // 开始解析
            readXml(resource);
        }
    }

    /**
     * 读取xml，并进行解析xml
     * @param path      xml的路径
     */
    @SuppressWarnings("unchecked")
    private void readXml(String path) {
        // 继续使用读取全局配置文件的工具读取 mapper.xml
        InputStream inputStream = Resources.getResourceAsStream(path);
        // 这里依然使用 SAX 解析 xml
        SAXReader sax = new SAXReader();
        try {
            Document root = sax.read(inputStream);
            Element rootElement = root.getRootElement();
            // 拿到 至关重要的信息 namespace
            String namespace = rootElement.attribute("namespace").getValue();

            try {
                // 这里使用反射获取namespace的Class对象
                Class<?> clazz = Class.forName(namespace);
                // 这里需要判断一下是否已经存在了Class
                if (!this.configuration.hasMapper(clazz)) {
                    // 将其加入到全局配置文件中
                    this.configuration.addMapper(clazz);
                }
            } catch (Exception ex) {
                throw new Exception("parse mapper failure  case: " + namespace, ex);
            }

            // 这里主要是用来实例化方法
            // 例如namespace：com.example.onebatis.mapper.UserMapper
            // 那么寻找对应的sql，其实就是拼接上方法名称
            // 如：sqlId：FindUserById
            // com.example.onebatis.mapper.UserMapper.FindUserById，这样就得到一个具体方法的路径
            String statementId = namespace.concat(".");

            List<Element> sqlElement = rootElement.elements();
            for (Element element : sqlElement) {
                String name = element.getName();
                // 当前版本只解析 CRUD 标签
                if (this.contains(name)) {
                    continue;
                }
                // 拿到id属性，此时的id属性是和mapper.xml中定义的方法名一致的
                String methodId = element.attribute("id").getValue();
                // 得到全路径
                String id = statementId + methodId;
                Attribute attribute = element.attribute("resultType");
                // 获取到方法返回的内容类型
                String resultType = attribute == null ? null : attribute.getValue();
                // 获取到入参
                String parameterType = getAttribute("parameterType", element);
                // 查看是否需要刷新二级缓存，但是本 demo中，并未实现二级缓存的功能
                boolean flushCache = ParseUtil.revert(getAttribute("flushCache", element));
                // 查看是否使用二级缓存
                boolean useCache = ParseUtil.revert(getAttribute("useCache", element));

                // 根据以上解析出来的内容，创建SqlBuilder对象，用来维护sql和接口的关系
                SqlBuilder sqlBuilder = new SqlBuilder().setId(methodId)
                        .setResultType(resultType)
                        .setParameterType(parameterType)
                        .setFlushCache(flushCache)
                        .setUseCache(useCache)
                        .setSql(getSql(element))
                        .setStatementId(id)
                        .setConfiguration(this.configuration)
                        .setCommandType(parseCommandType(name));

                // 将映射关系放入到全局配置文件中
                this.configuration.addSqlMapping(id, sqlBuilder);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 简单实现，只完成了 <select> <update> <delete> <insert> 的sql
     * @param name      标签名称
     * @return          是否包含在内
     */
    private boolean contains(String name) {
        return !name.contains("select")
                && !name.contains("update")
                && !name.contains("delete")
                && !name.contains("insert");
    }


    /**
     * 根据key获取val属性
     * @param tag       标签
     * @param element   节点
     * @return          val
     */
    private String getAttribute(String tag, Element element) {
        Attribute attribute = element.attribute(tag);
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    /**
     * 这里拿到sql，不做任何处理
     * @param element   XML 节点
     * @return          获取到sql
     */
    private String getSql(Element element) {
        // 这里获取一个标签内部的content属性
        String sql = ((DefaultText) (element.content().get(0))).getText();

        if (sql != null){
            // 将sql变为一行
            return sql.trim().replaceAll("\n+", "");
        }
        // 不执行下面的代码
        String s1 = sql.trim().replaceAll("\n+", "");
        String[] split = s1.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < split.length; i++) {
            String space = split[i];
            if (space.contains("#{")) {
                space = "?";
                if (i != split.length - 1){
                    space += ",";
                }
            }
            sb.append(space).append(" ");
        }
        return sb.toString();
    }

    /**
     * 类型转换，string转枚举
     * @param name      属性
     * @return          枚举
     */
    private SqlCommandType parseCommandType(String name) {
        if (Objects.equals("SELECT", name.toUpperCase())) {
            return SqlCommandType.SELECT;
        }
        if (Objects.equals("UPDATE", name.toUpperCase())) {
            return SqlCommandType.UPDATE;
        }
        if (Objects.equals("DELETE", name.toUpperCase())) {
            return SqlCommandType.DELETE;
        }
        if (Objects.equals("INSERT", name.toUpperCase())) {
            return SqlCommandType.INSERT;
        }
        return SqlCommandType.UNKNOWN;
    }
}
