package com.reactorAuth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactorAuth.dto.Result;
import com.reactorAuth.dto.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher matcher = new AntPathMatcher();

    // 白名单
    private static final List<String> WHITELIST = List.of("/auth/**", "/swagger-ui/**", "/v3/api-docs/**");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return WHITELIST.stream().anyMatch(p -> matcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException {
        try {
            String token = extractToken(request);
            if (token == null) {
                chain.doFilter(request, response);
                return;
            }

            Claims claims = tokenProvider.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            if (roles == null) roles = List.of();

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities));
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            writeError(response, ResultCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            writeError(response, ResultCode.TOKEN_INVALID);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) return bearer.substring(7);
        return null;
    }

    private void writeError(HttpServletResponse response, ResultCode code) throws IOException {
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Result.error(code));
    }
}
