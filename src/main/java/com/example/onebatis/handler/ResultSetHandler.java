package com.example.onebatis.handler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *      结果集处理器,将JDBC的结果ResultSet转换为 POJO
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/17 17:28
 **/
public class ResultSetHandler {


    /**
     * 处理结果集
     *
     * @param pojoClazz 被处理成这类的对象
     * @param resultSet 从mysql查询出来的内容
     * @param <T>       结果集
     * @return          转换为用户定义的类型
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> handlerResult(Class<?> pojoClazz, ResultSet resultSet) {
        try {
            // 结果
            List<T> result = new ArrayList<>();
            while(resultSet.next()){
                // 这里通过反射获取返回值类型实例
                T instance = (T) pojoClazz.newInstance();
                // 使用反射，为每一个属性赋值
                Field[] pojoFields = instance.getClass().getDeclaredFields();
                for (Field field : pojoFields) {
                    field.setAccessible(true);
                    Object fieldValue = getResult(resultSet, field);
                    field.set(instance, fieldValue);
                    field.setAccessible(false);
                }
                result.add(instance);
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            throw new RuntimeException("Query result mapping error." + ex);
        }
    }

    /**
     * 不同类型的结果
     * @param rs        查询结果
     * @param field     对象属性
     * @return          不同类型获取的不同结果
     * @throws SQLException 类型转换失败的异常
     */
    private Object getResult(ResultSet rs, Field field) throws SQLException {
        Class<?> type = field.getType();
        // 驼峰转下划线，实体类中如：userName 但是在数据库中为 user_name，根据 user_name 从结果集中获取数据
        String dataName = HumpToUnderline(field.getName());
        if (Integer.class == type) {
            return rs.getInt(dataName);
        } else if (String.class == type) {
            return rs.getString(dataName);
        } else if (Long.class == type) {
            return rs.getLong(dataName);
        } else if (Boolean.class == type) {
            return rs.getBoolean(dataName);
        } else if (Double.class == type) {
            return rs.getDouble(dataName);
        } else return rs.getString(dataName);
    }

    /**
     * 驼峰转下划线
     * @param para      驼峰
     * @return          下划线
     */
    public static String HumpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString().toUpperCase();
    }

}
