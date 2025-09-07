package com.yuetiku.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuetiku.dto.LoginRequest;
import com.yuetiku.dto.LoginResponse;
import com.yuetiku.dto.RegisterRequest;
import com.yuetiku.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    String register(RegisterRequest registerRequest);

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
}

