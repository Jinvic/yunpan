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
        System.out.println("\n\nUserController.addUser\n" + user.getUsername() + "-------" + user.getPassword() + "\n\n");
        //如果用户名或密码为空，返回注册页面，并提示消息
        if (user.getUsername() == null || user.getPassword() == null || user.getUsername().equals("") || user.getPassword().equals("")) {
            request.setAttribute("msg", "请输入用户名和密码");
            return "regist";
        } else {
            //用户名和密码不为空，执行保存操作
            boolean isSuccess = userService.addUser(user);
            //根据执行结果，为真开辟空间返回登录页面，为假用户名已注册返回注册页面
            if (isSuccess) {
                fileService.addNewNameSpace(request, user.getUsername());
                return "login";
            } else {
                request.setAttribute("msg", user.getUsername() + "被占用，请重新注册");
                return "regist";
            }
        }
    }

    //用户登录
    @RequestMapping("/login.action")
    public String userLogin(HttpServletRequest request, User user) {
        System.out.println("\n\nUserController.addUser\n" + user.getUsername() + "-------" + user.getPassword() + "\n\n");
        //如果用户名或密码为空，返回注册页面，并提示消息
        if (user.getUsername() == null || user.getPassword() == null || user.getUsername().equals("") || user.getPassword().equals("")) {
            request.setAttribute("msg", "请输入用户名和密码");
            return "login";
        }

        User exsitUser = userService.findUser(user);
        if (exsitUser != null) {
            //创建会话对象
            HttpSession session = request.getSession();
            //将用户对象存储到对话中
            session.setAttribute(User.NAMESPACE, exsitUser.getUsername());
            session.setAttribute("totalSize", exsitUser.getTotalSize());
            return "redirect:/index.action";
        } else {
            request.setAttribute("msg", "用户名或密码错误");
            return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/user/login.action";
    }
}

