import request from '@/utils/request'

export const getRoles = (params) => {
  return request({
    url: '/api/roles',
    method: 'get',
    params
  })
}

export const getRoleById = (id) => {
  return request({
    url: `/api/roles/${id}`,
    method: 'get'
  })
}

export const createRole = (data) => {
  return request({
    url: '/api/roles',
    method: 'post',
    data
  })
}

export const updateRole = (id, data) => {
  return request({
    url: `/api/roles/${id}`,
    method: 'put',
    data
  })
}

export const deleteRole = (id) => {
  return request({
    url: `/api/roles/${id}`,
    method: 'delete'
  })
}

// ========== 角色菜单权限 ==========

export const getRoleMenus = (roleId) => {
  return request({
    url: `/api/roles/${roleId}/menus`,
    method: 'get'
  })
}

export const assignRoleMenus = (roleId, menuIds) => {
  return request({
    url: `/api/roles/${roleId}/menus`,
    method: 'put',
    data: menuIds
  })
}

// ========== 角色权限配置 ==========

export const getRolePermissions = (roleId) => {
  return request({
    url: `/api/roles/${roleId}/permissions`,
    method: 'get'
  })
}

export const assignRolePermissions = (roleId, permIds) => {
  return request({
    url: `/api/roles/${roleId}/permissions`,
    method: 'put',
    data: permIds
  })
}
