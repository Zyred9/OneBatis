package com.example.onebatis.session;

import com.example.onebatis.builder.SqlBuilder;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * session 接口
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 13:59
 **/
public interface SqlSession {

    /**
     * 获取Mapper代理对象
     *
     * @param <T>       mapper
     * @param clazz     mapper对应的接口的Class对象
     * @return          代理类
     */
    <T> T getMapper(Class<T> clazz);

    /**
     * 得到configuration对象
     *
     * @return      获取到全局配置文件
     */
    Configuration getConfiguration();

    /**
     * 查询多条记录
     * @param sqlBuilder        sqlBuilder对象，里面包含了sql系列的内容
     * @param args              查询的参数
     * @param object            obj
     * @return                  查询返回的结果
     */
    <T> List<T> selectList(SqlBuilder sqlBuilder, Object[] args, Class object);

    /**
     * 单条查询
     * @param args          sql参数
     * @param sqlBuilder    sql
     * @return              查询结果
     */
    <T> T selectOne(SqlBuilder sqlBuilder, Object[] args, Class object);

    /**
     * 插入一条数据
     * @param args          sql参数
     * @param sqlBuilder    sql对象
     * @return              新增总条树
     */
    int insert(Object[] args, SqlBuilder sqlBuilder);

    /**
     * 更新方法
     * @param args          sql参数
     * @param sqlBuilder    sql对象
     * @return              更新总条数
     */
    int update(Object[] args, SqlBuilder sqlBuilder);

    /**
     * 删除方法
     * @param args          sql参数
     * @param sqlBuilder    sql对象
     * @return              删除总条数
     */
    int delete(Object[] args, SqlBuilder sqlBuilder);

    /**
     * 刷新 statement 对象
     * @return java.util.Collections#EMPTY_LIST
     */
    Object flushStatements();

}
