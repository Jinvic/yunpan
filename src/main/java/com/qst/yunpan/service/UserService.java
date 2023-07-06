package com.qst.yunpan.service;

import com.qst.yunpan.dao.UserDao;
import com.qst.yunpan.pojo.User;
import com.qst.yunpan.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public boolean addUser(User user) throws Exception {
        //先查询用户名是否存在
        User u = userDao.checkUser(user);
        if (u != null) {
            //用户名以及存在
            return false;
        }
        //将经过md5加密的密码重新赋值给用户对象
        user.setPassword(UserUtils.MD5(user.getPassword()));
        //初始化用户网盘空间
        user.setCountSize("0.0B");
        user.setTotalSize("10.0GB");
        userDao.addUser(user);
        return true;
    }
}
