package com.example.onebatis.binding;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>
 *      Mapper接口代理
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class MapperProxy<T> implements InvocationHandler, Serializable {

    /** sql会话 **/
    private final SqlSession sqlSession;
    /** 接口Class对象 **/
    private final Class<T> object;

    public MapperProxy(SqlSession sqlSession, Class<T> object) {
        this.sqlSession = sqlSession;
        this.object = object;
    }

    /**
     * 所有Mapper接口的方法调用都会走到这里
     *
     * @param proxy             代理对象
     * @param method            被执行的目标方法
     * @param args              方法参数
     * @return                  执行结果
     * @throws Throwable        异常执行
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 拿到方法所在的类的完整名称
        String mapperInterface = method.getDeclaringClass().getName();
        // 拿到方法名称
        String methodName = method.getName();
        // 拿到方法全路径
        String statementId = mapperInterface + "." + methodName;
        // 如果根据接口类型+方法名能找到映射的SQL，则执行SQL
        if (sqlSession.getConfiguration().hasStatement(statementId)) {
            // 通过映射关系找到对应的sql
            SqlBuilder sqlBuilder = sqlSession.getConfiguration().getMappedStatement(statementId);
            // 执行 CRUD 相关操作
            return cachedInvoker(method).invoke(proxy, method, args, sqlSession, sqlBuilder);
        }
        // 找不到 sql 和方法对应关系
        throw new RuntimeException("SQL and method relation could not be found.");
    }

    /**
     * 通过目标方法，封装成MapperMethodInvoker对象
     * @param method        目标方法
     * @return              MapperMethodInvoker
     */
    private MapperMethodInvoker cachedInvoker(Method method) {
        return new PlainMethodInvoker(new MapperMethod(method.getDeclaringClass(), method, sqlSession.getConfiguration(), object));
    }

    /**
     * 内部接口
     * 主要用于包装
     */
    interface MapperMethodInvoker {
        Object invoke(Object proxy, Method method, Object[] args,
                      SqlSession sqlSession, SqlBuilder sqlBuilder) throws Throwable;
    }

    /**
     * 静态内部内，对普通方法调用，其实就是执行 CRUD 操作
     */
    private static class PlainMethodInvoker implements MapperMethodInvoker {
        private final MapperMethod mapperMethod;

        public PlainMethodInvoker(MapperMethod mapperMethod) {
            super();
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args,
                             SqlSession sqlSession, SqlBuilder sqlBuilder) {
            // 调用 crud 执行操作方法
            return mapperMethod.execute(sqlSession, args, sqlBuilder);
        }
    }
}
