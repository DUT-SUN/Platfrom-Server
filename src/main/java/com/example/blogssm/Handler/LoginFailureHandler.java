package com.example.blogssm.Handler;

import cn.hutool.json.JSONUtil;
import com.example.blogssm.Exception.CaptchaException;
import com.example.blogssm.common.AjaxResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/31  19:27
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
//        System.out.println("进入失败了");
        String errorMessage = "用户名或密码错误";
        AjaxResult result;
        if (e instanceof CaptchaException) {
            errorMessage = "验证码错误";
            result =  AjaxResult.fail(0,errorMessage);
        } else {
            result =  AjaxResult.fail(0,errorMessage);
        }
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
