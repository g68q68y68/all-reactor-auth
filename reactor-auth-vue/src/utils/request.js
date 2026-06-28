import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'

/**
 * Token 过期/无效：清空登录态并跳转登录页
 */
function handleTokenExpired(message) {
  const authStore = useAuthStore()
  authStore.clearAuth()
  ElMessage.error(message || '登录已过期，请重新登录')
  setTimeout(() => {
    window.location.href = '/login'
  }, 800)
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器 — 注入 Bearer token
request.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 — 解包 Result<T>，识别 token 过期
request.interceptors.response.use(
  response => {
    const body = response.data
    if (body && typeof body.code === 'number') {
      if (body.code === 200) {
        return body.data
      }
      // Token 过期/无效 → 直接跳登录
      if (body.code === 40101 || body.code === 40102) {
        handleTokenExpired(body.message)
        return Promise.reject(new Error(body.message || '登录已过期'))
      }
      // 其他业务错误
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body
  },
  error => {
    const responseData = error.response?.data

    // HTTP 401 + code 40101/40102 → Token 过期/无效
    if (responseData?.code === 40101 || responseData?.code === 40102) {
      handleTokenExpired(responseData.message)
      return Promise.reject(error)
    }

    // 通用 HTTP 401（无 token 或未登录场景，非 token 过期）
    if (error.response?.status === 401) {
      handleTokenExpired('登录已过期，请重新登录')
      return Promise.reject(error)
    }

    // 其他错误提示
    const message = responseData?.message || error.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
