package com.reactorAuth.controller;

import com.reactorAuth.dto.MenuTreeNode;
import com.reactorAuth.dto.Result;
import com.reactorAuth.dto.ResultCode;
import com.reactorAuth.entity.Menu;
import com.reactorAuth.service.MenuService;
import com.reactorAuth.utils.LoginUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@Slf4j
public class MenuController extends BaseController<Menu, MenuService> {

    private final MenuService menuService;


    @Autowired
    private LoginUserUtil loginUserUtil;

    public MenuController(MenuService menuService) {
        super(menuService);
        this.menuService = menuService;
    }

    /**
     * 返回当前用户可见的菜单树。
     * ADMIN 角色 → 全部菜单；其他角色 → 按 role_menus 中间表过滤
     */
    @GetMapping("/tree")
    public Mono<Result<List<MenuTreeNode>>> tree() {
        return loginUserUtil.getUserDetails()
                .flatMap(userDetails -> {
                    Long userId = userDetails.getUserId();
                    Set<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());
                    return menuService.buildTreeForUser(userId, roles);
                })
                .map(Result::success)
                .switchIfEmpty(Mono.just(Result.error(ResultCode.UNAUTHORIZED, "未登录")))
                .doOnError(e -> log.error("获取菜单树失败: {}", e.getMessage()));
    }
}
