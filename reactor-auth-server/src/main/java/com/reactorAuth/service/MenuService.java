package com.reactorAuth.service;

import com.reactorAuth.dto.MenuTreeNode;
import com.reactorAuth.entity.Menu;
import com.reactorAuth.repository.MenuRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService extends BaseService<Menu, MenuRepository> {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        super(menuRepository, "菜单");
        this.menuRepository = menuRepository;
    }

    /**
     * 构建菜单树（全部启用菜单，ADMIN 用）
     */
    public Mono<List<MenuTreeNode>> buildTree() {
        return menuRepository.findAllEnabledMenus()
                .collectList()
                .map(this::convertToTree);
    }

    /**
     * 按用户角色构建菜单树。
     * ADMIN → 全部菜单；其他角色 → 通过 role_menus 中间表过滤
     */
    public Mono<List<MenuTreeNode>> buildTreeForUser(Long userId, Set<String> roles) {
        if (roles != null && roles.contains("ROLE_ADMIN")) {
            return buildTree();
        }
        Flux<Menu> menuFlux = menuRepository.findMenusByUserId(userId);
        // 如果用户没有分配任何菜单，返回空列表
        return menuFlux.collectList().map(this::convertToTree);
    }

    private List<MenuTreeNode> convertToTree(List<Menu> menus) {
        // 1. 将所有实体转为树节点（无 children）
        List<MenuTreeNode> allNodes = menus.stream()
                .map(this::toTreeNode)
                .collect(Collectors.toList());

        // 2. 按 parentId 分组
        Map<Long, List<MenuTreeNode>> parentIdMap = allNodes.stream()
                .filter(n -> n.getParentId() != null)
                .collect(Collectors.groupingBy(MenuTreeNode::getParentId));

        // 3. 为每个节点设置 children，并排序
        allNodes.forEach(node -> {
            List<MenuTreeNode> children = parentIdMap.getOrDefault(node.getId(), new ArrayList<>());
            children.sort((a, b) -> {
                int s1 = a.getSortOrder() != null ? a.getSortOrder() : 0;
                int s2 = b.getSortOrder() != null ? b.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            });
            node.setChildren(children);
        });

        // 4. 返回排序后的根节点（parentId == null）
        return allNodes.stream()
                .filter(n -> n.getParentId() == null)
                .sorted((a, b) -> {
                    int s1 = a.getSortOrder() != null ? a.getSortOrder() : 0;
                    int s2 = b.getSortOrder() != null ? b.getSortOrder() : 0;
                    return Integer.compare(s1, s2);
                })
                .collect(Collectors.toList());
    }

    private MenuTreeNode toTreeNode(Menu menu) {
        return MenuTreeNode.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .path(menu.getPath())
                .redirect(menu.getRedirect())
                .name(menu.getName())
                .component(menu.getComponent())
                .title(menu.getTitle())
                .icon(menu.getIcon())
                .type(menu.getType())
                .requiresAuth(menu.getRequiresAuth())
                .sortOrder(menu.getSortOrder())
                .status(menu.getStatus())
                .hidden(menu.getHidden())
                .children(new ArrayList<>())
                .build();
    }

    @Override
    protected void prepareCreate(Menu menu) {
        menu.setId(null);
        if (menu.getStatus() == null) menu.setStatus(1);
        if (menu.getHidden() == null) menu.setHidden(false);
        if (menu.getRequiresAuth() == null) menu.setRequiresAuth(true);
        if (menu.getSortOrder() == null) menu.setSortOrder(0);
        if (menu.getType() == null) menu.setType("MENU");
    }

    @Override
    protected void mergeEntity(Menu existing, Menu incoming) {
        if (incoming.getParentId() != null) existing.setParentId(incoming.getParentId());
        if (incoming.getPath() != null) existing.setPath(incoming.getPath());
        if (incoming.getRedirect() != null) existing.setRedirect(incoming.getRedirect());
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getComponent() != null) existing.setComponent(incoming.getComponent());
        if (incoming.getTitle() != null) existing.setTitle(incoming.getTitle());
        if (incoming.getIcon() != null) existing.setIcon(incoming.getIcon());
        if (incoming.getType() != null) existing.setType(incoming.getType());
        if (incoming.getRequiresAuth() != null) existing.setRequiresAuth(incoming.getRequiresAuth());
        if (incoming.getSortOrder() != null) existing.setSortOrder(incoming.getSortOrder());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
        if (incoming.getHidden() != null) existing.setHidden(incoming.getHidden());
    }
}
