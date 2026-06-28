package com.reactorAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeNode {
    private Long id;
    private Long parentId;
    private String path;
    private String redirect;
    private String name;
    private String component;
    private String title;
    private String icon;
    private String type;
    private Boolean requiresAuth;
    private Integer sortOrder;
    private Integer status;
    private Boolean hidden;
    private List<MenuTreeNode> children;
}
