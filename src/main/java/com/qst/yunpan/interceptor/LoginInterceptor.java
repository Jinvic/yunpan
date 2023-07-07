package com.qst.yunpan.interceptor;

import com.qst.yunpan.utils.UserUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    //请求前进行拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        //获取请求的url
        String url = request.getRequestURI();

        //判断请求的url字符串中是否包含如下字符串
        if (url.indexOf("login.action") >= 0
                || url.indexOf("regist.action") >= 0
                || url.indexOf("doRegist.action") >= 0
                || url.indexOf("share.action") >= 0
                || url.indexOf("getShareFiles.action") >= 0
                || url.indexOf("download.action") >= 0
                || url.indexOf("loginForApp.action") >= 0
                || url.indexOf("getAppFiles.action") >= 0
                || url.indexOf("uploadForApp.action") >= 0) {
            //放行，可以执行请求
            return true;
        }

        //从会话中获取用户名，如果已经登录过，可以获取到
        String username = UserUtils.getUsername(request);
        if (username != null) {
            return true;
        }

        response.sendRedirect("user/login.action");
        return false;
    }

}