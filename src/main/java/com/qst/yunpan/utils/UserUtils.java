package com.qst.yunpan.utils;

import com.qst.yunpan.pojo.User;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserUtils {
    public static String MD5(String password) {
        if (password != null) {
            return DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        } else {
            return null;
        }
    }

    public static String getUsername(HttpServletRequest request) {
        //获取会话对象
        HttpSession session = request.getSession();
        //返回用户名
        return session.getAttribute(User.NAMESPACE).toString();
    }
}
