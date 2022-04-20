package com.yibao.securitydemo.config;

import com.yibao.securitydemo.filter.LoginFilter;
import com.yibao.securitydemo.filter.LoginKaptchaFilter;
import com.yibao.securitydemo.handler.CustomAuthenticationEntryPoint;
import com.yibao.securitydemo.handler.CustomAuthenticationFailureHandler;
import com.yibao.securitydemo.handler.CustomAuthenticationSuccessHandler;
import com.yibao.securitydemo.handler.CustomLogoutSuccessHandler;
import com.yibao.securitydemo.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * @author yibao
 * @create 2022 -04 -12 -15:32
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 自定义数据源
    private final CustomUserDetailService customUserDetailService;

    // 密码加密方式（如果此处声明，则默认固定使用声明的方式）
//    @Bean
//    public PasswordEncoder BcryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Autowired
    public SecurityConfig(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    // 重写，使用自定义的 UserDetailsService
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService);
    }

    // 将自定义的 AuthenticationManager 在工厂中进行暴露，以方便在任何位置都可以注入使用
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 自定义 filter (交予工厂管理)
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();   // 账号密码验证
//        LoginKaptchaFilter loginKaptchaFilter = new LoginKaptchaFilter();   // 账号密码 + 验证码


        loginFilter.setUsernameParameter("username");       // 指定接收 json -- 灵活写法
        loginFilter.setPasswordParameter("password");
//        loginKaptchaFilter.setKaptchaParamter("kaptcha");       // 验证码
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler());      // 认证成功处理
        loginFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());      // 认证失败处理
        loginFilter.setFilterProcessesUrl("/doLogin");    // 指定认证 url

        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .mvcMatchers("/getVerifyCode").permitAll()    // 放行：生成验证码
                .anyRequest().authenticated()      // 所有请求都需要认证
                .and()
                .formLogin()
//                .and()
//                .rememberMe()   // 开启记住我功能
//                .rememberMeServices(rememberMeServices())
                .and()
                .logout()
                .logoutRequestMatcher(new OrRequestMatcher(       // 自定义登出url，并设置请求方式
                        new AntPathRequestMatcher("/logout", HttpMethod.GET.name()),
                        new AntPathRequestMatcher("/logout", HttpMethod.DELETE.name())
                ))
                .logoutSuccessHandler(new CustomLogoutSuccessHandler())   // 注销成功处理
                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())    // 认证异常处理
                .and()
                .csrf().disable();     // 跨域

        // 用自定义的 filter 替换原有的账户密码验证 filter ( 并保证filter执行顺序 )
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    // 记住我
//    public RememberMeServices rememberMeServices() {
//        return new PersistentTokenBasedRememberMeServices(
//                "key",     // 自定义生成令牌的 key，默认为 UUID
//                userDetailsService()    // 认证数据源
//                , new JdbcTokenRepositoryImpl());    // 令牌存储方式
//    }
}
