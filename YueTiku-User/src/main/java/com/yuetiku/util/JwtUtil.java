package com.yuetiku.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:iFishSecretKeyForHS512AlgorithmMustBeAtLeast512BitsLongToEnsureSecurityAndCompliance2024}")
    private String secret;

    @Value("${jwt.expiration:86400}")  // 默认24小时
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800}")  // 默认7天
    private Long refreshExpiration;

    /**
     * 获取密钥
     */
    private SecretKey getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(secret.getBytes());
        } catch (Exception e) {
            log.error("JWT密钥生成失败: {}", e.getMessage());
            throw new RuntimeException("JWT密钥配置错误", e);
        }
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取指定声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("解析JWT令牌失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 检查令牌是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String username, Long userId, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        claims.put("type", "access");
        return createToken(claims, username, expiration * 1000);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration * 1000);
    }

    /**
     * 创建令牌
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证令牌
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> {
            Object userId = claims.get("userId");
            if (userId == null) {
                throw new IllegalArgumentException("令牌中缺少userId声明");
            }
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            if (userId instanceof Long) {
                return (Long) userId;
            }
            if (userId instanceof String) {
                return Long.valueOf((String) userId);
            }
            throw new IllegalArgumentException("userId声明类型不支持: " + userId.getClass());
        });
    }

    /**
     * 从令牌中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> {
            Object userType = claims.get("userType");
            return userType != null ? userType.toString() : "USER";
        });
    }

    /**
     * 从令牌中获取令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> {
            Object type = claims.get("type");
            return type != null ? type.toString() : "access";
        });
    }

    /**
     * 刷新令牌
     */
    public String refreshToken(String refreshToken) {
        try {
            Claims claims = getAllClaimsFromToken(refreshToken);
            String tokenType = (String) claims.get("type");

            if (!"refresh".equals(tokenType)) {
                throw new JwtException("Invalid refresh token type");
            }

            String username = claims.getSubject();
            Long userId = getUserIdFromToken(refreshToken);
            Object userTypeObj = claims.get("userType");
            String userType = userTypeObj != null ? userTypeObj.toString() : "USER";

            return generateAccessToken(username, userId, userType);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new JwtException("Invalid refresh token");
        }
    }

    /**
     * 获取访问令牌过期时间（秒）
     */
    public Long getAccessTokenExpiration() {
        return expiration;
    }

    /**
     * 获取刷新令牌过期时间（秒）
     */
    public Long getRefreshTokenExpiration() {
        return refreshExpiration;
    }
}