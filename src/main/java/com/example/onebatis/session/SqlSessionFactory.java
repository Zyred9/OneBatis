package com.example.onebatis.session;

/**
 * <p>
 *      创建sqlSession对象工厂接口
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 13:59
 **/
public interface SqlSessionFactory {

    /**
     *  打开一个session 会话
     * @return  会话
     */
    SqlSession openSession();

}
