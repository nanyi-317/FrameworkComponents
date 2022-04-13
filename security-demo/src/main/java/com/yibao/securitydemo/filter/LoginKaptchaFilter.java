package com.yibao.securitydemo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibao.securitydemo.exception.KaptchaException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义登录认证 filter ，替换原有的 UsernamePasswordAuthenticationFilter
 *
 * @author yibao
 * @create 2022 -04 -12 -15:40
 */
public class LoginKaptchaFilter extends UsernamePasswordAuthenticationFilter {

    // 定义验证码字段，给个默认值，灵活写法
    public static final String FORM_KAPTCHA_KEY = "kaptcha";
    private String kaptchaParamter = FORM_KAPTCHA_KEY;

    public String getKaptchaParamter() {
        return kaptchaParamter;
    }

    public void setKaptchaParamter(String kaptchaParamter) {
        this.kaptchaParamter = kaptchaParamter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1.判断是否为 post 请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 2.判断是否为 json 请求格式
        if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            try {
                // 3.从 json 中获取登录信息并进行认证
                Map<String, String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String username = userInfo.get(getUsernameParameter());
                String password = userInfo.get(getPasswordParameter());
                String kaptcha = userInfo.get(getKaptchaParamter());    // 获取验证码 -- 灵活写法

                // 4.获取 session 中的验证码
                String verifyCode = (String) request.getSession().getAttribute("kaptcha");
                if (!ObjectUtils.isEmpty(kaptcha) && !ObjectUtils.isEmpty(verifyCode) && kaptcha.equalsIgnoreCase(verifyCode)) {
                    // 5.验证码通过，进行账号密码校验
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                }
                // 6.验证码错误异常
                throw new KaptchaException("验证码不匹配!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 如果不是 json ，直接调用父类的验证方式
        return super.attemptAuthentication(request, response);
    }
}
