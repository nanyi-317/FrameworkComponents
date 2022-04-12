package com.yibao.security.config;

import com.yibao.security.handler.MyAuthenticationFailureHandler;
import com.yibao.security.handler.MyAuthenticationSuccessHandler;
import com.yibao.security.handler.MyLogoutSuccessHandler;
import com.yibao.security.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * @author yibao
 * @create 2022 -04 -06 -14:39
 */
@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    // 使用自定义的 UserDetailService 作为数据源
    private final MyUserDetailService myUserDetailService;

    @Autowired
    public WebSecurityConfigurer(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    // 默认全局 AuthenticationManager
//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("bbb").password("{noop}123456").roles("admin1").build());
//        return manager;
//    }

    // 自定义 AuthenticationManager ， 需要指明 UserDetailsService
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService());
        auth.userDetailsService(myUserDetailService);
    }

    // 作用：将自定义的 AuthenticationManager 在工厂中进行暴露，以方便在任何位置都可以注入使用
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

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
