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

<<<<<<< HEAD
    public User findUser(User user) {
        try{
            user.setPassword(UserUtils.MD5(user.getPassword()));//用户密码MD5加密
            User existUser = userDao.findUser(user);
            return existUser;
        }catch (Exception e){
=======

    public User findUser(User user) {
        try {
            //将用户的密码进行md5加密
            user.setPassword(UserUtils.MD5(user.getPassword()));
            User exsitUser = userDao.findUser(user);
            return exsitUser;
        } catch (Exception e) {
>>>>>>> temp
            e.printStackTrace();
            return null;
        }
    }
<<<<<<< HEAD
}
=======


    public String getCountSize(String username) {
        String countSize = null;
        try {
            countSize = userDao.getCountSize(username);
        } catch (Exception e) {
            e.printStackTrace();
            return countSize;
        }
        return countSize;
    }
}

>>>>>>> temp
