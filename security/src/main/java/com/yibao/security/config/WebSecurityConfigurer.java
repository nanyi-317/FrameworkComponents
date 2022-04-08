package com.yibao.security.config;

import com.yibao.security.handler.MyAuthenticationFailureHandler;
import com.yibao.security.handler.MyAuthenticationSuccessHandler;
import com.yibao.security.handler.MyLogoutSuccessHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * @author yibao
 * @create 2022 -04 -06 -14:39
 */
@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .mvcMatchers("/index").permitAll()   // 放行
                .anyRequest().authenticated()        // 全部拦截
                .and()
                .formLogin()                        // 以表单形式
                .successHandler(new MyAuthenticationSuccessHandler())   // 认证成功时的处理
                .failureHandler(new MyAuthenticationFailureHandler())   //认证失败时的处理
                .and()
                .logout()
                .logoutUrl("/logout")   // 默认
                .invalidateHttpSession(true)    // 退出时，session是否失效  -- 默认 true
                .clearAuthentication(true)      // 退出时，是否清楚认证信息  -- 默认 true
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/yibao","GET"),     // 配置多个注销请求
                        new AntPathRequestMatcher("/logout","GET"),
                        new AntPathRequestMatcher("aa","POST")
                ))
                .logoutSuccessHandler(new MyLogoutSuccessHandler())  // 注销成功时的处理
                .and()
                .csrf().disable();   // 禁止 csrf 跨站请求保护
    }
}
