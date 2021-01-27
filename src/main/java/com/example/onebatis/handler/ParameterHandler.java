package com.example.onebatis.handler;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * <p>
 *      sql参数处理
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/17 17:16
 **/
public class ParameterHandler {

    private final PreparedStatement preparedStatement;

    public ParameterHandler(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    /**
     * 从方法中获取参数，遍历设置SQL中的？占位符
     * @param parameters        sql参数
     * @param mapping           映射关系
     */
    public void setParameters(Object[] parameters, LinkedHashMap<String, Class<?>> mapping) {
        try {
            // PreparedStatement的序号是从1开始的
            for (int i = 0; i < parameters.length; i++) {
                int k = i + 1;
                if (parameters[i] instanceof Integer) {
                    preparedStatement.setInt(k, (Integer) parameters[i]);
                } else if (parameters[i] instanceof Long) {
                    preparedStatement.setLong(k, (Long) parameters[i]);
                } else if (parameters[i] instanceof String) {
                    preparedStatement.setString(k, String.valueOf(parameters[i]));
                } else if (parameters[i] instanceof Boolean) {
                    preparedStatement.setBoolean(k, (Boolean) parameters[i]);
                } else {
                    Object objects = parameters[i];

                    Set<String> keys = mapping.keySet();
                    int counter = 1;
                    for (String key : keys) {
                        Field field = objects.getClass().getDeclaredField(key);
                        field.setAccessible(true);
                        Object value = field.get(objects);
                        // 两次解析出来的结果一致
                        if (field.getType() != mapping.get(key)) {
                            continue;
                        }
                        if (field.getType() == Integer.class) {
                            preparedStatement.setInt(counter, (Integer) value);
                        } else if (field.getType() == Long.class) {
                            preparedStatement.setLong(counter, (Long) value);
                        } else if (field.getType() == String.class) {
                            preparedStatement.setString(counter, (String) value);
                        } else if (field.getType() == Boolean.class) {
                            preparedStatement.setBoolean(counter, (Boolean) value);
                        }
                        counter ++;
                        field.setAccessible(false);
                    }
                }
            }
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
