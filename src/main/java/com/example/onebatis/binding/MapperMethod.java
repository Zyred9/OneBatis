package com.example.onebatis.binding;

import com.example.onebatis.builder.SqlBuilder;
import com.example.onebatis.session.Configuration;
import com.example.onebatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class MapperMethod {

    private SqlBuilder sqlBuilder;
    private final MethodSignature method;
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
        Object result = null;
        switch (sqlBuilder.getCommandType()) {
            case INSERT: {
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
            case SELECT:
                if (method.returnsVoid()) {
                    // executeWithResultHandler(sqlSession, args);
                    result = null;
                } else if (method.returnsMany()) {
                    // 查询返回集合方法
                    result = executeForMany(sqlSession, args, sqlBuilder);
                } else if (method.returnsMap()) {
                    // 返回结果为Map的方法
//                    result = executeForMap(sqlSession, args);
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
        if (result == null && method.returnType().isPrimitive() && !method.returnsVoid()) {
            throw new RuntimeException("Mapper method '" + this.sqlBuilder.getStatementId()
                    + " attempted to return null from a method with a primitive return type (" + method.returnType() + ").");
        }
        return result;
    }

    private Object executeForMany(SqlSession sqlSession, Object[] args, SqlBuilder sqlBuilder) {
        return sqlSession.selectList(sqlBuilder, args, this.object);
    }


    public static class MethodSignature {

        private final boolean returnsMany;
        private final boolean returnsMap;
        private final boolean returnsVoid;
        private final boolean returnsOptional;
        private final Class<?> returnType;
        private final String methodName;

        public MethodSignature(Method method) {

            this.returnType = method.getReturnType();

            this.returnsVoid = void.class.equals(this.returnType);
            this.returnsMany = Collection.class.isAssignableFrom(this.returnType) || this.returnType.isArray();
            this.returnsOptional = Optional.class.equals(this.returnType);
            this.returnsMap = Map.class.equals(this.returnType);
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

    private Object rowCountResult(int rowCount) {
        final Object result;
        if (method.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            result = (long)rowCount;
        } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new RuntimeException("Mapper method '" +method.getName() + "' has an unsupported return type: " + method.getReturnType());
        }
        return result;
    }

}
