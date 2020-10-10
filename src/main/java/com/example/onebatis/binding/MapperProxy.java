package com.example.onebatis.binding;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private SqlSession sqlSession;
    private Class object;

    public MapperProxy(SqlSession sqlSession, Class object) {
        this.sqlSession = sqlSession;
        this.object = object;
    }

    /**
     * 所有Mapper接口的方法调用都会走到这里
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String mapperInterface = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String statementId = mapperInterface + "." + methodName;
        // 如果根据接口类型+方法名能找到映射的SQL，则执行SQL
        if (sqlSession.getConfiguration().hasStatement(statementId)) {
            SqlBuilder sqlBuilder = sqlSession.getConfiguration().getMappedStatement(statementId);
            // 执行sql查询
//            return sqlSession.selectList(sqlBuilder, args, object);
            return cachedInvoker(method).invoke(proxy, method, args, sqlSession, sqlBuilder);
        }
//        return method.invoke(proxy, args);
        // 找不到 sql 和方法对应关系
        throw new RuntimeException("SQL and method relation could not be found.");
    }

    private MapperMethodInvoker cachedInvoker(Method method) {
        return new PlainMethodInvoker(new MapperMethod(method.getDeclaringClass(), method, sqlSession.getConfiguration(), object));
    }

    interface MapperMethodInvoker {
        Object invoke(Object proxy, Method method, Object[] args,
                      SqlSession sqlSession, SqlBuilder sqlBuilder) throws Throwable;
    }

    private static class PlainMethodInvoker implements MapperMethodInvoker {
        private MapperMethod mapperMethod;

        public PlainMethodInvoker(MapperMethod mapperMethod) {
            super();
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args,
                             SqlSession sqlSession, SqlBuilder sqlBuilder) {
            return mapperMethod.execute(sqlSession, args, sqlBuilder);
        }
    }
}
