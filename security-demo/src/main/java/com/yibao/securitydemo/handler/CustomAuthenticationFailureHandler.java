package com.yibao.securitydemo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 自定义登录失败后的处理
 * @author yibao
 * @create 2022 -04 -12 -16:23
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msg", "登录失败："+exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        String string = new ObjectMapper().writeValueAsString(result);
        response.getWriter().print(string);
    }
}
