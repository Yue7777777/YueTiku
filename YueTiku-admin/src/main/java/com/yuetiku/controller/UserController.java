package com.yuetiku.controller;

import com.yuetiku.common.Result;
import com.yuetiku.context.BaseContext;
import com.yuetiku.dto.LoginRequest;
import com.yuetiku.dto.LoginResponse;
import com.yuetiku.dto.RegisterRequest;
import com.yuetiku.entity.User;
import com.yuetiku.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册请求: {}", registerRequest.getEmail());
        String result = userService.register(registerRequest);
        return Result.success(result);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getEmail());
        LoginResponse response = userService.login(loginRequest);
        return Result.success("登录成功", response);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<LoginResponse.UserInfo> getUserInfo() {
        // 从BaseContext获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            return Result.unauthorized("用户未登录");
        }

        // 根据用户ID查询用户信息
        User user = userService.getById(currentUserId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            return Result.forbidden("用户已被禁用");
        }

        // 构建用户信息响应
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setStatus(user.getStatus());

        log.info("获取用户信息成功: {}", user.getUsername());
        return Result.success("获取用户信息成功", userInfo);
    }
}
