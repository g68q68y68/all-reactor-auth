import request from '@/utils/request'

export const getUsers = (params) => {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

export const getUserById = (id) => {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

export const createUser = (data) => {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

export const updateUser = (id, data) => {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}
