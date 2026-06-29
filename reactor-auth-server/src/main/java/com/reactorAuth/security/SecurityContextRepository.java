//package com.reactorAuth.security;
//
//import com.reactorAuth.config.WhitelistProperties;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class SecurityContextRepository implements ServerSecurityContextRepository {
//
//    private final JwtTokenProvider tokenProvider;
//    private final WhitelistProperties whitelistProperties;
//
//    @Override
//    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
//        // 不需要保存到session，使用无状态JWT
//        return Mono.empty();
//    }
//
//    @Override
//    public Mono<SecurityContext> load(ServerWebExchange exchange) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getPath().value();
//
//        // 白名单路径不处理
//        if (whitelistProperties.isWhiteListPath(path)) {
//            return Mono.empty();
//        }
//
//        String token = getTokenFromRequest(request);
//        if (token == null) {
//            return Mono.empty();
//        }
//
//        return tokenProvider.validateToken(token)
//                .flatMap(valid -> {
//                    if (!valid) {
//                        return Mono.empty();
//                    }
//
//                    return tokenProvider.getUsernameFromToken(token)
//                            .flatMap(username ->
//                                tokenProvider.getUserIdFromToken(token)
//                                        .zipWith(tokenProvider.getRolesFromToken(token))
//                            )
//                            .map(tuple -> {
//                                Long userId = tuple.getT1();
//                                Set<String> roles = tuple.getT2();
//
//                                List<SimpleGrantedAuthority> authorities = roles.stream()
//                                        .map(SimpleGrantedAuthority::new)
//                                        .collect(Collectors.toList());
//
//                                UsernamePasswordAuthenticationToken authentication =
//                                        new UsernamePasswordAuthenticationToken(
//                                                userId.toString(),
//                                                null,
//                                                authorities
//                                        );
//
//                                return new SecurityContextImpl(authentication);
//                            });
//                });
//    }
//
//    private String getTokenFromRequest(ServerHttpRequest request) {
//        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//}
