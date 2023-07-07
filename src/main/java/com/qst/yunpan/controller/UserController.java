package com.qst.yunpan.controller;

import com.qst.yunpan.pojo.User;
import com.qst.yunpan.service.FileService;
import com.qst.yunpan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户控制器类
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    @RequestMapping("/regist.action")
    public String toRegist() {
        return "regist";
    }
    //进过dispatcherservlet杰斯后得到了页面的实际物理路径
    //WEB-INF/jsp/regist.jsp

    //用户注册
    @RequestMapping("/doRegist.action")
    public String addUser(HttpServletRequest request, User user) throws Exception {
        System.out.println(user.getUsername() + "-------" + user.getPassword());
        //如果用户名或密码为空，返回注册页面，并提示消息
        if (user.getUsername() == null || user.getPassword() == null
                || user.getUsername().equals("") || user.getPassword().equals("")) {
            request.setAttribute("msg", "请输入用户名和密码");
            return "regist";
        } else {
            //用户名和密码不为空，执行保存操作
            boolean isSuccess = userService.addUser(user);
//            根据执行结果，为真开辟空间返回登录页面，为假用户名已注册返回注册页面
            if (isSuccess) {
                fileService.addNewNameSpace(request, user.getUsername());
                return "login";
            } else {
                request.setAttribute("msg", user.getUsername() + "被占用，请重新注册");
                return "regist";
            }
        }

    }
    @RequestMapping("/login.action")
    public String userLogin(HttpServletRequest req,User user){
        if(user.getUsername()==null||user.getPassword()==null||user.getUsername().equals("")||user.getPassword().equals("")){
            req.setAttribute("msg","请输入用户名或者密码");
            return "login";
        }
        User existUser = userService.findUser(user);
        if(existUser!=null){
            HttpSession session = req.getSession();
            session.setAttribute(User.NAMESPACE,existUser);
            session.setAttribute("totalSize",existUser.getTotalSize());
            return "redirect:/index.action";//服务端转发是一次请求，forward:index.action,request对象中公用,客户端重定向重新发起请求，无法共用
        }else{
            req.setAttribute("msg","账号/密码错误");
            return "login";
        }
    }
}
