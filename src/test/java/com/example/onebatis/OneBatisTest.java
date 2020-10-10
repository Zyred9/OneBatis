package com.example.onebatis;

import com.example.onebatis.builder.Resources;
import com.example.onebatis.session.SqlSession;
import com.example.onebatis.session.SqlSessionFactory;
import com.example.onebatis.session.SqlSessionFactoryBuilder;
import com.example.onebatis.test.entity.User;
import com.example.onebatis.test.mapper.UserMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/23 17:26
 **/
public class OneBatisTest {

    private SqlSessionFactory build;

    @Before
    public void before() {
        // 读取配置文件，使用 hutool 完成的
        InputStream stream = Resources.getResourceAsStream("classpath:mybatis-config.xml");
        // 通过配置文件获取 SqlSessionFactory，放入成员变量中，提供后续事件
        build = new SqlSessionFactoryBuilder().build(stream);
    }


    @Test
    public void selectList() {
        SqlSession sqlSession = build.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> page = mapper.getUserPage("10086");
        page.stream().forEach(System.out::println);
    }

    @Test
    public void insert() {
        SqlSession sqlSession = build.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        User user = new User();
        user.setUserName("苟七");
        user.setPassword("12345");
        user.setAddress("深圳");
        user.setPhone("10086");

        int i = mapper.inertUser(user);
        System.out.println(i);
    }

    @Test
    public void update () {
        SqlSession sqlSession = build.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        User user = new User();
        user.setId("5");
        user.setUserName("哈八");
        user.setPassword("321654");

        int i = mapper.updateUser(user);
        System.out.println(i);
    }

    @Test
    public void delete () {
        SqlSession sqlSession = build.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        int i = mapper.deleteUser("6");
        System.out.println(i);
    }


    @Test
    public void jdbcInsert() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onebatis?serverTimezone=GMT%2B8", "root", "root");
        String sql = "INSERT INTO t_user (user_name, password, address, phone) values (?, ?, ?, ?);";
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        ptmt.setString(1, "haha");
        ptmt.setString(2, "123456");
        ptmt.setString(3, "重慶");
        ptmt.setString(4, "10086");

        ptmt.execute();
    }
}
