package com.yibao.securitydemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试类
 * @author yibao
 * @create 2022 -04 -12 -15:15
 */
@RestController
public class TestController {

    /**
     * 方法：测试
     * @return
     */
    @RequestMapping("/test")
    public String test() {
        System.out.println("testing....");
        return "test ok !";
    }
}
