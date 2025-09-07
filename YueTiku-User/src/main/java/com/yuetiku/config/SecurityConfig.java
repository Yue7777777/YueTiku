package com.yuetiku.config;

import com.yuetiku.context.BaseContext;
import com.yuetiku.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Spring Security 配置类
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * 密码编码器
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT认证过滤器
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，因为我们使用 JWT
                .csrf(AbstractHttpConfigurer::disable)

                // 禁用默认的登录表单
                .formLogin(AbstractHttpConfigurer::disable)

                // 禁用 HTTP Basic 认证
                .httpBasic(AbstractHttpConfigurer::disable)

                // 设置会话管理策略为无状态
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许所有 OPTIONS 请求（CORS 预检请求）
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // 允许认证相关接口无需认证
                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/user/login").permitAll()
                        // 允许错误页面
                        .requestMatchers("/error").permitAll()
                        // 题库管理接口需要认证
                        .requestMatchers("/categories/**").authenticated()
                        .requestMatchers("/questions/**").authenticated()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * JWT认证过滤器内部类
     */
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;

        public JwtAuthenticationFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }


        //让JWT令牌在异步/错误分派的时候也执行
        @Override
        protected boolean shouldNotFilterAsyncDispatch() {
            return false;
        }

        @Override
        protected boolean shouldNotFilterErrorDispatch() {
            return false;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            String requestURI = request.getRequestURI();
            log.debug("处理请求: {} {}", request.getMethod(), requestURI);

            try {
                // 从请求头中获取Authorization
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    log.debug("发现JWT令牌，开始验证");

                    try {
                        // 先获取用户名，用于验证
                        String username = jwtUtil.getUsernameFromToken(token);
                        log.debug("从令牌中获取用户名: {}", username);

                        // 验证JWT令牌
                        if (jwtUtil.validateToken(token, username)) {
                            try {
                                // 从令牌中获取用户信息
                                Long userId = jwtUtil.getUserIdFromToken(token);
                                String userType = jwtUtil.getUserTypeFromToken(token);

                                log.debug("JWT令牌验证成功，用户ID: {}, 用户类型: {}", userId, userType);

                                //存储用户id信息
                                BaseContext.setCurrentId(userId);

                                // 创建认证对象
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                username,
                                                null,
                                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                                        );

                                // 设置认证信息到安全上下文
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.debug("认证信息已设置到安全上下文");
                            } catch (Exception userInfoException) {
                                log.error("获取用户信息失败: {}", userInfoException.getMessage());
                                // 即使获取用户信息失败，也创建基本的认证对象
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                username,
                                                null,
                                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                                        );
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.debug("使用基本认证信息设置到安全上下文");
                            }
                        } else {
                            log.warn("JWT令牌验证失败");
                        }
                    } catch (Exception tokenException) {
                        log.error("JWT令牌处理异常: {}", tokenException.getMessage());
                    }
                } else {
                    log.debug("请求中没有找到有效的Authorization头");
                }
            } catch (Exception e) {
                // 记录错误但不阻止请求继续
                log.error("JWT认证过滤器处理异常: {}", e.getMessage(), e);
            }
            filterChain.doFilter(request, response);
        }
    }

}
