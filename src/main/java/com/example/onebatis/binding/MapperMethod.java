package com.example.onebatis.binding;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.session.Configuration;
import com.example.onebatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 *      MapperMethod
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class MapperMethod {

    /** sqlBuilder **/
    private final SqlBuilder sqlBuilder;
    /** 执行的方法 **/
    private final MethodSignature method;
    /** sqlBuilder **/
    private final Class<?> object;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration, Class object) {
        this.sqlBuilder = configuration.getMappedStatement(mapperInterface.getName().concat(".").concat(method.getName()));
        this.method = new MethodSignature(method);
        this.object = object;
    }

    /**
     *  执行查询的方法
     * @param sqlSession        sql 会话
     * @param args              请求参数
     * @param sqlBuilder        解析完毕后的sql对象，包含一条sql所有的信息
     * @return                  Object
     */
    public Object execute(SqlSession sqlSession, Object[] args, SqlBuilder sqlBuilder) {
        // 初始化返回结果
        Object result = null;
        // 拿到查询类型，INSERT/UPDATE/DELETE/SELECT
        switch (sqlBuilder.getCommandType()) {
            case INSERT: {
                // 这就调用了执行会话的返回结果，INSERT/UPDATE/DELETE返回结果都是统一的，那么处理方法其实是一样的
                result = this.rowCountResult(sqlSession.insert(args, sqlBuilder));
                break;
            }
            case UPDATE: {
                result = this.rowCountResult(sqlSession.update(args, sqlBuilder));
                break;
            }
            case DELETE: {
                result = this.rowCountResult(sqlSession.delete(args, sqlBuilder));
                break;
            }
            // 查询的返回结果会有不同类型，那么就要根据不同类型进行处理
            case SELECT:
                // 拿到方法返回结果，将类型封装到结果中
                if (method.returnsVoid()) {
                    // executeWithResultHandler(sqlSession, args);
                    result = null;
                }
                // 返回结果是多个的情况
                else if (method.returnsMany()) {
                    // 查询返回集合方法
                    result = executeForMany(sqlSession, args, sqlBuilder);
                }
                // return map
                else if (method.returnsMap()) {
                    // 返回结果为Map的方法
                    result = executeForMap(sqlSession, args);
                } else {
                    // 普通查询的方法
                    result = sqlSession.selectOne(sqlBuilder, args, this.object);
                }
                break;
            case FLUSH:
                result = sqlSession.flushStatements();
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + this.sqlBuilder.getStatementId());
        }
        // 如果查询条件为null，并且返回值是原始类型，并且返回值不是void，那么就证明查询结果为null
        if (result == null && method.returnType().isPrimitive() && !method.returnsVoid()) {
            throw new RuntimeException("Mapper method '" + this.sqlBuilder.getStatementId()
                    + " attempted to return null from a method with a primitive return type (" + method.returnType() + ").");
        }
        // 返回查询结果
        return result;
    }

    /**
     * TODO
     * @param sqlSession    会话
     * @param args          入参
     * @return              结果
     */
    private Map<Object, Object> executeForMap(SqlSession sqlSession, Object[] args) {
        return null;
    }

    /**
     * 执行多个结果的返回
     * @param sqlSession    执行会话
     * @param args          方法入参
     * @param sqlBuilder    sql
     * @return              查询结果
     */
    private List<Object> executeForMany(SqlSession sqlSession, Object[] args, SqlBuilder sqlBuilder) {
        return sqlSession.selectList(sqlBuilder, args, this.object);
    }


    /**
     * 返回类型和方法名称的封装对象
     */
    public static class MethodSignature {

        /** 是否返回多个结果 **/
        private final boolean returnsMany;
        /** 是否返回Map **/
        private final boolean returnsMap;
        /** 是否返回void **/
        private final boolean returnsVoid;
        /** 是否返回Optional类型 **/
        private final boolean returnsOptional;
        /** 返回值类型 **/
        private final Class<?> returnType;
        /** 方法名称 **/
        private final String methodName;

        /**
         * 构造器，通过目标方法获取参数
         * @param method        目标方法
         */
        public MethodSignature(Method method) {
            // 获取返回类型
            this.returnType = method.getReturnType();
            this.returnsVoid = void.class.equals(this.returnType);
            this.returnsMany = Collection.class.isAssignableFrom(this.returnType) || this.returnType.isArray();
            this.returnsOptional = Optional.class.equals(this.returnType);
            this.returnsMap = Map.class.equals(this.returnType);
            // 拿到方法名称
            this.methodName = method.getName();
        }

        public boolean returnsMany() {
            return this.returnsMany;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public boolean returnsMap() {
            return this.returnsMap;
        }

        public boolean returnsVoid() {
            return this.returnsVoid;
        }

        public boolean returnsOptional() {
            return this.returnsOptional;
        }

        public Class<?> returnType() {
            return this.returnType;
        }

        public String getName(){
            return this.methodName;
        }
    }

    /**
     * 返回结果的条数统计
     * @param rowCount      sql执行结果
     * @return              不同类型的转化
     */
    private Object rowCountResult(int rowCount) {
        final Object result;
        // void
        if (method.returnsVoid()) {
            result = null;
        }
        // int
        else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            result = rowCount;
        }
        // long
        else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            result = (long)rowCount;
        }
        // boolean
        else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            result = rowCount > 0;
        }
        // insert 方法返回结果就这么几种，如果其他的类型，那么肯定是错误的
        else {
            throw new RuntimeException("Mapper method '" +method.getName() + "' has an unsupported return type: " + method.getReturnType());
        }
        return result;
    }

}
