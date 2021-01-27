package com.example.onebatis.parsing;

import com.example.onebatis.util.ReflectUtil;

import java.sql.Statement;
import java.util.LinkedHashMap;

/**
 * 动态 sql 处理  只包含 CRUD 操作，不包含复杂sql拼接
 * <p>
 *
 * @author zyred
 * @since 0.1
 * </p>
 **/
public class DynamicSqlNode {

    /** sql **/
    private String sql;
    /** 参数 **/
    private final Object[] args;
    private static final String space_regex = " +";
    private static final String space = " ";
    private static final String holder_prefix = "#{";
    private static final String holder_suffix = "}";
    private static final String comma = ",";
    private static final String holder = "?";
    private static final String final_holder = "#{?}";

    /** 这里可能会设计到线程安全问题 **/
    public LinkedHashMap<String, Class<?>> mapping = new LinkedHashMap<>();

    public DynamicSqlNode(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    /**
     * 解析占位符
     * @return      解析后的sql，带占位符
     */
    public String parsePlaceholder() {
        if (sql == null || sql.equals("")) {
            throw new RuntimeException("SQL can not be null : " + sql);
        }
        // 将多行sql 变成一行
        sql = sql.replaceAll("\n+", "");
        String[] elements = sql.split(space_regex);
        StringBuilder sb = new StringBuilder();
        for (String element : elements) {
            if (!element.contains(holder_prefix)) {
                sb.append(element.trim()).append(space);
                continue;
            }
            int indexPrefix = element.indexOf(holder_prefix) + 2;
            int indexSuffix = element.indexOf(holder_suffix);

            String property = element.substring(indexPrefix, indexSuffix);
            // 得到属性与属性类型映射关系
            propertyClass(property);
            element = element.replaceAll(property, holder).replace(final_holder, holder);
            if (element.contains(comma)) {
                sb.append(element).append(space);
            } else {
                sb.append(element);
            }
        }
        return sb.toString();
    }

    /**
     * 解析属性的Java类型
     *
     * @param properties        #{userName} -> userName(入参)
     */
    private void propertyClass(String properties) {
        for (Object arg : this.args) {
            if (isBaseDataType(arg.getClass())) {
                continue;
            }
            // 得到properties的类型
            Class<?> clazz = ReflectUtil.propertyType(arg, properties);
            // 参数名称和类型的结果封装
            mapping.put(properties, clazz);
        }
    }


    private boolean isBaseDataType (Object target) {
        if (target == Byte.class) {
            return true;
        } else if (target == Short.class){
            return true;
        } else if (target == Integer.class){
            return true;
        } else if (target == Long.class){
            return true;
        } else if (target == Float.class){
            return true;
        } else if (target == Double.class){
            return true;
        } else if (target == Boolean.class){
            return true;
        } else if (target == Character.class){
            return true;
        } else return target == String.class;
    }
}
