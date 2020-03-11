package com.caili.service;

import com.caili.pojo.User;

import java.util.List;

/**
 * @Author: huey
 * @Desc:
 */
public interface UserService {

    User queryByOid(String openId);

    void add(User user);

    int updateById(User user);

    List<User> queryByUsername(String username);

    List<User> queryByMobile(String mobile);
}
