import request from '@/utils/request'

export const getMenus = (params) => {
  return request({
    url: '/api/menus',
    method: 'get',
    params
  })
}

export const getMenuTree = () => {
  return request({
    url: '/api/menus/tree',
    method: 'get'
  })
}

export const getMenuById = (id) => {
  return request({
    url: `/api/menus/${id}`,
    method: 'get'
  })
}

export const createMenu = (data) => {
  return request({
    url: '/api/menus',
    method: 'post',
    data
  })
}

export const updateMenu = (id, data) => {
  return request({
    url: `/api/menus/${id}`,
    method: 'put',
    data
  })
}

export const deleteMenu = (id) => {
  return request({
    url: `/api/menus/${id}`,
    method: 'delete'
  })
}
