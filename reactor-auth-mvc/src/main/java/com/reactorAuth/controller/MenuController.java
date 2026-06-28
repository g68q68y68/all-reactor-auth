package com.reactorAuth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reactorAuth.dto.*;
import com.reactorAuth.entity.Menu;
import com.reactorAuth.mapper.MenuMapper;
import com.reactorAuth.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuMapper menuMapper;
    private final UserRoleMapper userRoleMapper;

    @GetMapping
    public Result<PageResult<Menu>> page(@RequestParam(defaultValue="0") int page,
                                          @RequestParam(defaultValue="10") int size) {
        Page<Menu> p = menuMapper.selectPage(new Page<>(page+1, size),
                new LambdaQueryWrapper<Menu>().orderByAsc(Menu::getSortOrder));
        return Result.success(PageResult.of(p));
    }

    @GetMapping("/tree")
    public Result<List<MenuTreeNode>> tree() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_ADMIN"));

        List<Menu> menus;
        if (isAdmin) {
            menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getStatus, 1).orderByAsc(Menu::getSortOrder));
        } else {
            Long userId = Long.valueOf(auth.getPrincipal().toString());
            menus = menuMapper.findByUserId(userId);
        }
        return Result.success(buildTree(menus));
    }

    @GetMapping("/{id}")
    public Result<Menu> findById(@PathVariable Long id) { return Result.success(menuMapper.selectById(id)); }

    @PostMapping public Result<Menu> create(@RequestBody Menu m) { m.setId(null); menuMapper.insert(m); return Result.success(m); }

    @PutMapping("/{id}")
    public Result<Menu> update(@PathVariable Long id, @RequestBody Menu m) { m.setId(id); menuMapper.updateById(m); return Result.success(menuMapper.selectById(id)); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { menuMapper.deleteById(id); return Result.success(); }

    private List<MenuTreeNode> buildTree(List<Menu> menus) {
        List<MenuTreeNode> nodes = menus.stream().map(m -> {
            MenuTreeNode n = new MenuTreeNode();
            n.setId(m.getId()); n.setParentId(m.getParentId());
            n.setPath(m.getPath()); n.setRedirect(m.getRedirect()); n.setName(m.getName());
            n.setComponent(m.getComponent()); n.setTitle(m.getTitle()); n.setIcon(m.getIcon());
            n.setType(m.getType()); n.setRequiresAuth(m.getRequiresAuth());
            n.setSortOrder(m.getSortOrder()); n.setStatus(m.getStatus()); n.setHidden(m.getHidden());
            n.setChildren(new ArrayList<>());
            return n;
        }).collect(Collectors.toList());

        Map<Long, List<MenuTreeNode>> map = nodes.stream()
                .filter(n -> n.getParentId() != null)
                .collect(Collectors.groupingBy(MenuTreeNode::getParentId));

        nodes.forEach(n -> {
            List<MenuTreeNode> children = map.getOrDefault(n.getId(), new ArrayList<>());
            children.sort(Comparator.comparingInt(m -> m.getSortOrder() != null ? m.getSortOrder() : 0));
            n.setChildren(children);
        });

        return nodes.stream().filter(n -> n.getParentId() == null)
                .sorted(Comparator.comparingInt(m -> m.getSortOrder() != null ? m.getSortOrder() : 0))
                .collect(Collectors.toList());
    }
}
