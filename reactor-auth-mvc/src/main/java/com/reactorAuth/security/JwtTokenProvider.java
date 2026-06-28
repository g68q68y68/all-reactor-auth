package com.reactorAuth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.jwt.expiration}")
    private long expiration;

    private SecretKey getKey() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }

    public String generateToken(Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Date now = new Date();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roles", user.getRoles());
        claims.put("permissions", user.getPermissions());
        return Jwts.builder().claims(claims).subject(user.getUsername())
                .issuedAt(now).expiration(new Date(now.getTime() + expiration))
                .signWith(getKey()).compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token) {
        try { parseToken(token); return true; }
        catch (ExpiredJwtException e) { return false; }
        catch (Exception e) { return false; }
    }

    public boolean isExpired(String token) {
        try { parseToken(token); return false; }
        catch (ExpiredJwtException e) { return true; }
        catch (Exception e) { return false; }
    }
}
