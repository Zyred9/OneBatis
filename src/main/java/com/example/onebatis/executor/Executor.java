package com.example.onebatis.executor;

import com.example.onebatis.builder.SqlBuilder;

import java.util.List;

/**
 * <p>
 *      sql执行器，这里只定义了两个执行的方法，那么先归一下类
 *
 *      属于query类型的： select
 *      属于update类型的： insert/update/delete
 *
 *      所以执行器中定义的方法是在 数据库层面定义的
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/25 8:39
 **/
public interface Executor {


    /**
     * 查询接口
     * @param parameters    sql 参数
     * @param sqlBuilder    sql 绑定对象，里面涵盖 一个<select>标签的内容
     * @param <T>           返回任意对象类型
     * @return              执行结果
     */
    <T> List<T> query(Object[] parameters, SqlBuilder sqlBuilder);

    /**
     * 更新操作
     * @param parameters        更新的内容
     * @param sqlBuilder        sql 语句对象
     * @return                  > 0 success  < 0 failure
     */
    int update(Object[] parameters, SqlBuilder sqlBuilder);

}
