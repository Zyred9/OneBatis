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
 * mapper.xml 解析器
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 8:34
 **/
public class MapperParser {

    private List<String> resources;
    private Configuration configuration;

    public MapperParser(List<String> resources, Configuration configuration) {
        this.resources = resources;
        this.configuration = configuration;
    }

    public void parse() {
        for (String resource : resources) {
            readXml(resource);
        }
    }

    private void readXml(String path) {
        InputStream inputStream = Resources.getResourceAsStream(path);
        SAXReader sax = new SAXReader();
        try {
            Document root = sax.read(inputStream);
            Element rootElement = root.getRootElement();
            String namespace = rootElement.attribute("namespace").getValue();

            try {
                Class<?> clazz = Class.forName(namespace);
                if (!this.configuration.hasMapper(clazz)) {
                    this.configuration.addMapper(clazz);
                }

            } catch (Exception ex) {
                throw new Exception("parse mapper failure  case: " + namespace, ex);
            }

            String statementId = namespace + ".";

            List<Element> sqlElement = rootElement.elements();
            for (Element element : sqlElement) {
                String name = element.getName();
                // 当前版本只解析 CRUD 标签
                if (this.contains(name)) {
                    continue;
                }
                String methodId = element.attribute("id").getValue();
                String id = statementId + methodId;
                Attribute attribute = element.attribute("resultType");
                String resultType = attribute == null ? null : attribute.getValue();
                String parameterType = getAttribute("parameterType", element);
                boolean flushCache = ParseUtil.revert(getAttribute("flushCache", element));
                boolean useCache = ParseUtil.revert(getAttribute("useCache", element));

                SqlBuilder sqlBuilder = new SqlBuilder().setId(methodId)
                        .setResultType(resultType)
                        .setParameterType(parameterType)
                        .setFlushCache(flushCache)
                        .setUseCache(useCache)
                        .setSql(getSql(element))
                        .setStatementId(id)
                        .setConfiguration(this.configuration)
                        .setCommandType(parseCommandType(name));

                this.configuration.addSqlMapping(id, sqlBuilder);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean contains(String name) {
        if (name.contains("select")
                || name.contains("update")
                || name.contains("delete")
                || name.contains("insert")
        ) {
            return false;
        }
        return true;
    }


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
     * @return
     */
    private String getSql(Element element) {
        String sql = ((DefaultText) (element.content().get(0))).getText();

        if (sql != null){
            return sql.trim().replaceAll("\n", "");
        }

        // 不执行下面的代码
        String s1 = sql.trim().replaceAll("\n", "");
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
