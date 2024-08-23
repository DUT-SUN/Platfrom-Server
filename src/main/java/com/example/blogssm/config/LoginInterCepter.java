//package com.example.blogssm.config;
//
//import com.example.blogssm.common.AppVarible;
//import com.example.blogssm.entity.UserInfo;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
///**
// * 功能描述:登录拦截器
// * <p>
// * 成略在胸，良计速出
// *
// * @author SUN
// * @date 2023/04/21  17:34
// */
//
//public class LoginInterCepter implements HandlerInterceptor {
//    /**
//    * @explain
//    * @param request true->用户已登录 false->用户未登录
//    * @param response
//    * @param handler
//    * @return boolean
//    * @author DUT-SUN
//    * @date   2023/4/21
//    */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session=request.getSession(false);
//        if(session!=null && session.getAttribute(AppVarible.USER_SESSION_KEY)!=null){
//            //用户已登录
//            System.out.println("当前登录用户为"+((UserInfo)session.getAttribute(AppVarible.USER_SESSION_KEY)).getUsername());
//            return true;
//        }
//        //跳转到登录页
//        response.sendRedirect("/login.html");
//        return false;
//    }
//}
