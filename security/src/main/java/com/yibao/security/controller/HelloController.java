package com.yibao.security.controller;

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
        return "hello security --- 保护资源";
    }

}
