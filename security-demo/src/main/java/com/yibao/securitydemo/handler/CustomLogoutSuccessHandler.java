package com.yibao.securitydemo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 自定义注销成功处理
 * @author yibao
 * @create 2022 -04 -12 -16:35
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msg", "注销成功");
        result.put("authentication", authentication);  // 拿到用户权限信息
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpStatus.OK.value());
        String string = new ObjectMapper().writeValueAsString(result);
        response.getWriter().print(string);
    }
}
