package com.example.onebatis.util;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * <p>
 *     反射工具类
 * </p>
 *
 * @author zyred
 * @createTime 2020/8/12 16:07
 **/
public class ReflectUtil {


    /**
     * 获取元素的类型
     * @param target        目标类
     * @param property      属性名称
     * @return              返回property在目标对象中的类型Class
     */
    public static Class<?> propertyType(Object target, String property) {
        if (Objects.isNull(target)) {
            throw new RuntimeException("Target can not be null.");
        }
        Class<?> clazz = target.getClass();

        String msg = "";
        try {
            msg = property;
            Field field;
            try {
                field = clazz.getDeclaredField(property);
            } catch (NoSuchFieldException ex) {
                Class<?> superClazz = clazz.getSuperclass();
                field = superClazz.getDeclaredField(property);
            }
            field.setAccessible(true);
            return field.getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("Property [%s] not found. ", msg));
        }
    }
}
