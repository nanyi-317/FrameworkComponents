package com.yibao.securitydemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yibao.securitydemo.mapper")
public class SecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityDemoApplication.class, args);
	}

}
