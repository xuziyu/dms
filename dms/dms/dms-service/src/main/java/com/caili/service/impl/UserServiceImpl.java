package com.caili.service.impl;

import com.caili.mapper.UserExtMapper;
import com.caili.pojo.User;
import com.caili.pojo.UserExample;
import com.caili.service.UserService;
import io.swagger.annotations.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: huey
 * @Desc:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserExtMapper UserMapper;


    public User queryByOid(String openId) {
        UserExample example = new UserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return UserMapper.selectOneByExample(example);
    }


    public void add(User user) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        UserMapper.insertSelective(user);
    }

    public int updateById(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return UserMapper.updateByPrimaryKeySelective(user);
    }


    public List<User> queryByUsername(String username) {
        UserExample example = new UserExample();
        //就是去验证名字 判断名字 然后装到里面去
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return UserMapper.selectByExample(example);
    }

    public List<User> queryByMobile(String mobile) {
        UserExample example = new UserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return UserMapper.selectByExample(example);
    }

}
