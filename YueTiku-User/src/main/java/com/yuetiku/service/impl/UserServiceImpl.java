package com.yuetiku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuetiku.dto.LoginRequest;
import com.yuetiku.dto.LoginResponse;
import com.yuetiku.dto.RegisterRequest;
import com.yuetiku.entity.User;
import com.yuetiku.mapper.UserMapper;
import com.yuetiku.service.UserService;
import com.yuetiku.util.JwtUtil;
import com.yuetiku.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterRequest registerRequest) {
        // 验证密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        User existingUserByUsername = findByUsername(registerRequest.getUsername());
        if (existingUserByUsername != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        User existingUserByEmail = findByEmail(registerRequest.getEmail());
        if (existingUserByEmail != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(PasswordUtil.encodePassword(registerRequest.getPassword()));
        user.setStatus(1); // 正常状态
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        boolean saved = save(user);
        if (!saved) {
            throw new RuntimeException("用户注册失败");
        }

        log.info("用户注册成功: {}", user.getUsername());
        return "注册成功";
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据邮箱查找用户
        User user = findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 验证密码
        if (!PasswordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT令牌
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), "USER");
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId());

        // 构建登录响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpiration());

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setStatus(user.getStatus());
        response.setUserInfo(userInfo);

        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    @Override
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return getOne(queryWrapper);
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return getOne(queryWrapper);
    }
}

