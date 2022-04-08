package com.yibao.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yibao
 * @create 2022 -04 -06 -14:32
 */
@RestController
public class IndexController {

    @RequestMapping("/index")
    public String index() {
        System.out.println("index security");
        return "index security --- 公共资源";
    }
}
