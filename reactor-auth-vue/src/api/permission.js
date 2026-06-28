import request from '@/utils/request'

export const getPermissions = (params) => {
  return request({
    url: '/api/permissions',
    method: 'get',
    params
  })
}

export const getPermissionById = (id) => {
  return request({
    url: `/api/permissions/${id}`,
    method: 'get'
  })
}

export const createPermission = (data) => {
  return request({ url: '/api/permissions', method: 'post', data })
}

export const updatePermission = (id, data) => {
  return request({ url: `/api/permissions/${id}`, method: 'put', data })
}

export const deletePermission = (id) => {
  return request({ url: `/api/permissions/${id}`, method: 'delete' })
}

/** 刷新权限缓存到 Redis */
export const refreshPermissionCache = () => {
  return request({ url: '/api/permissions/refresh-cache', method: 'post' })
}
