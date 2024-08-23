//package com.example.blogssm.common;
//
//import com.example.blogssm.entity.UserInfo;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
///**
// * 功能描述 当前登录用户相关的操作
// * <p>
// * 成略在胸，良计速出
// *
// * @author SUN
// * @date 2023/05/15  10:26
// */
//public class UserSessionUtils {
//    /**
//    * @explain 得到当前登录用户
//    * @param request
//    * @return com.example.blogssm.entity.UserInfo
//    * @author DUT-SUN
//    * @date   2023/5/15
//    */
//   public static UserInfo getSessUser(HttpServletRequest request){
//       HttpSession session=request.getSession(false);
//       if(session!=null && session.getAttribute(AppVarible.USER_SESSION_KEY)!=null){
//           return (UserInfo)session.getAttribute(AppVarible.USER_SESSION_KEY);
//       }
//       return null;
//   }
//}
