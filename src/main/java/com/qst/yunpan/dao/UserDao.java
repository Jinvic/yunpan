package com.qst.yunpan.dao;

import com.qst.yunpan.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    //用户注册
    public void addUser(User user) throws Exception;

    //检查用户名是否已被注册
    public User checkUser(User user) throws Exception;

    //用户登录
    public User findUser(User user) throws Exception;

    String getCountSize(String username) throws Exception;
}
