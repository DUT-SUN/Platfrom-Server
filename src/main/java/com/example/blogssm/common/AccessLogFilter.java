package com.example.blogssm.common;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * @date 2024/04/14  21:12
 */


@Component
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger accessLogger = LogManager.getLogger("accessLogger");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String logStr = String.format("path:%s|method:%s|ua:%s|ip:%s",
                request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr());

        accessLogger.info(logStr);
        filterChain.doFilter(request, response);
    }
}
