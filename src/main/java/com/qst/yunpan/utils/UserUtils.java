package com.qst.yunpan.utils;

<<<<<<< HEAD
import org.springframework.util.DigestUtils;

=======
import com.qst.yunpan.pojo.User;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

>>>>>>> temp
public class UserUtils {
    public static String MD5(String password) {
        if (password != null) {
            return DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        } else {
            return null;
        }
    }
<<<<<<< HEAD
=======

    public static String getUsername(HttpServletRequest request) {
        //获取会话对象
        HttpSession session = request.getSession();
        //返回用户名
        return session.getAttribute(User.NAMESPACE).toString();
    }
>>>>>>> temp
}
