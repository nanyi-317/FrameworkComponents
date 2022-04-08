package com.yibao.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 自定义注销登录处理器
 * @author yibao
 * @create 2022 -04 -08 -11:39
 */
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msg", "注销成功");
        result.put("status", 200);
        result.put("authentication", authentication);  // 拿到用户权限信息
        response.setContentType("application/json;charset=UTF-8");
        String string = new ObjectMapper().writeValueAsString(result);
        response.getWriter().print(string);
    }
}
