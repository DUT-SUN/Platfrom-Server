package com.example.blogssm.config;

import com.example.blogssm.Exception.CaptchaException;
import com.example.blogssm.Handler.LoginFailureHandler;
//import com.example.blogssm.common.RedisUtil;
import com.example.blogssm.common.RedisCache;
import com.example.blogssm.constants.Const;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/31  20:01
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
   private RedisCache redisCache;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String url = httpServletRequest.getRequestURI();
        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {
            // 校验验证码
            try {
                validate(httpServletRequest);
            } catch (CaptchaException e) {
                // 交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
    // 校验验证码逻辑
    private void validate(HttpServletRequest httpServletRequest) {
        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("userKey");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码错误");
        }
        if (!code.equals(redisCache.hget(Const.CAPTCHA_KEY, key))) {
            throw new CaptchaException("验证码错误");
        }
        // 若验证码正确，执行以下语句
        // 一次性使用
        redisCache.hdel(Const.CAPTCHA_KEY, key);
    }
}
