package com.yibao.security.controller;

import com.yibao.security.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 controller
 * @author yibao
 * @create 2022 -04 -01 -15:05
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        System.out.println("hello security");
        // 获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        System.out.println("用户身份 --- " + principal.getUsername());
        System.out.println("凭证 --- " + authentication.getCredentials());
        System.out.println("权限 --- " + authentication.getAuthorities());

        // 子线程
//        new Thread(()->{
//            // 获取用户信息
//            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
//            User principal1 = (User) authentication1.getPrincipal();
//            System.out.println("用户身份 --- " + principal1.getUsername());
//            System.out.println("凭证 --- " + authentication1.getCredentials());
//            System.out.println("权限 --- " + authentication1.getAuthorities());
//        }).start();

        return "hello security --- 保护资源";
    }
}
