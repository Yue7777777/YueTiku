package com.yuetiku.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 */
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}