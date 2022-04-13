package com.yibao.securitydemo.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author yibao
 * @create 2022 -04 -13 -18:13
 */
public class KaptchaException extends AuthenticationException {
    public KaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KaptchaException(String msg) {
        super(msg);
    }
}
