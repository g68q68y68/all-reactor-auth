import request from '@/utils/request'

// ========== 枚举类型 ==========

export const getEnumTypes = (params) => {
  return request({ url: '/api/enum-types', method: 'get', params })
}

export const getEnumTypesWithValues = () => {
  return request({ url: '/api/enum-types/with-values', method: 'get' })
}

export const getEnumTypeById = (id) => {
  return request({ url: `/api/enum-types/${id}`, method: 'get' })
}

export const createEnumType = (data) => {
  return request({ url: '/api/enum-types', method: 'post', data })
}

export const updateEnumType = (id, data) => {
  return request({ url: `/api/enum-types/${id}`, method: 'put', data })
}

export const deleteEnumType = (id) => {
  return request({ url: `/api/enum-types/${id}`, method: 'delete' })
}

// ========== 枚举值 ==========

export const getEnumValues = (params) => {
  return request({ url: '/api/enum-values', method: 'get', params })
}

export const getEnumValueById = (id) => {
  return request({ url: `/api/enum-values/${id}`, method: 'get' })
}

export const createEnumValue = (data) => {
  return request({ url: '/api/enum-values', method: 'post', data })
}

export const updateEnumValue = (id, data) => {
  return request({ url: `/api/enum-values/${id}`, method: 'put', data })
}

export const deleteEnumValue = (id) => {
  return request({ url: `/api/enum-values/${id}`, method: 'delete' })
}
