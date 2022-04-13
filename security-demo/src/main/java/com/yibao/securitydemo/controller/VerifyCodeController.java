package com.yibao.securitydemo.controller;

import com.google.code.kaptcha.Producer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;

/**
 * 验证码
 *
 * @author yibao
 * @create 2022 -04 -13 -16:13
 */
@RestController
public class VerifyCodeController {
    private final Producer producer;

    @Autowired
    public VerifyCodeController(Producer producer) {
        this.producer = producer;
    }

    /**
     * 方法：生成验证码，并返回 base64 (可放入 session、redis..)
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("/getVerifyCode")
    public String getVerifyCode(HttpSession session) throws Exception{
        // 1.生成验证码
        String text = producer.createText();
        // 2.放入 session
        session.setAttribute("kaptcha", text);
        // 3.生成图片
        BufferedImage image = producer.createImage(text);
        FastByteArrayOutputStream fos = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", fos);
        // 4.返回 base64
        return Base64.encodeBase64String(fos.toByteArray());
    }
}
