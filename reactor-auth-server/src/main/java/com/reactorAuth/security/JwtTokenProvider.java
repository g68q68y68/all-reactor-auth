package com.reactorAuth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Mono<String> generateToken(Authentication authentication) {
        return Mono.fromCallable(() -> {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userDetails.getUserId());
            claims.put("username", userDetails.getUsername());
            claims.put("roles", userDetails.getRoles());
            claims.put("permissions", userDetails.getPermissions());
            claims.put("email", userDetails.getEmail());
            claims.put("fullName", userDetails.getFullName());

            return Jwts.builder()
                    .claims(claims)
                    .subject(userDetails.getUsername())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        });
    }

    public Mono<String> generateRefreshToken(Authentication authentication) {
        return Mono.fromCallable(() -> {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + refreshExpiration);

            return Jwts.builder()
                    .subject(userDetails.getUsername())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        });
    }

    public Mono<Claims> getClaimsFromToken(String token) {
        return Mono.fromCallable(() ->
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
        ).doOnError(error -> log.error("解析JWT失败: {}", error.getMessage()));
    }

    public Mono<String> getUsernameFromToken(String token) {
        return getClaimsFromToken(token)
                .map(Claims::getSubject);
    }

    public Mono<Long> getUserIdFromToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> claims.get("userId", Long.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<Set<String>> getRolesFromToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> {
                    List<String> rolesList = (List<String>) claims.get("roles");
                    return new HashSet<>(rolesList);
                });
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                Date expiration = claims.getExpiration();
                if (expiration.before(new Date())) {
                    log.warn("Token已过期");
                    return false;
                }
                return true;
            } catch (Exception e) {
                log.error("Token验证失败: {}", e.getMessage());
                return false;
            }
        });
    }

    public Mono<Boolean> isTokenExpired(String token) {
        return getClaimsFromToken(token)
                .map(claims -> claims.getExpiration().before(new Date()));
    }
}
