package com.qst.yunpan.controller;

import com.qst.yunpan.pojo.User;
import com.qst.yunpan.service.UserService;
import com.qst.yunpan.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import com.qst.yunpan.utils.UserUtils;
@Controller
public class IndexController {
    @Autowired
    private UserService userService;

    /**
     * 主页面页面
     * @param request
     * @return
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request){
        String username = UserUtils.getUsername(request);
        String countSize = userService.getCountSize(username);
        request.setAttribute("countSize", countSize);
        return "index";
    }
}