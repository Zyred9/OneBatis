package com.example.onebatis.test.mapper;

import com.example.onebatis.test.entity.User;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public interface UserMapper {

    List<User> getUserPage(String phone);

    int inertUser(User user);

    int updateUser(User user);

    int deleteUser(String id);
}
