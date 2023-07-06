package com.qst.yunpan.utils;

import org.springframework.util.DigestUtils;

public class UserUtils {
    public static String MD5(String password) {
        if (password != null) {
            return DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        } else {
            return null;
        }
    }
}
