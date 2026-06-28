import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null,
    userInfo: null,
    roles: [],
    permissions: [],
    expiresIn: 0
  }),

  getters: {
    isAuthenticated: (state) => !!state.accessToken,
    username: (state) => state.userInfo?.username || '',
    userId: (state) => state.userInfo?.userId || '',
    fullName: (state) => state.userInfo?.fullName || '',
    isAdmin: (state) => state.roles?.includes('ROLE_ADMIN') || false,
    isUser: (state) => state.roles?.includes('ROLE_USER') || false
  },

  actions: {
    async login(loginData) {
      try {
        const response = await loginApi(loginData)
        const { accessToken, userInfo, expiresIn } = response

        this.accessToken = accessToken
        this.userInfo = userInfo
        this.roles = userInfo?.roles || []
        this.permissions = userInfo?.permissions || []
        this.expiresIn = expiresIn || 86400

        this.persist()
        ElMessage.success('登录成功')
        return { success: true, data: response }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
        return { success: false, error: error.message }
      }
    },

    async logout() {
      try {
        await logoutApi()
      } catch (error) {
        // 忽略登出接口错误
      } finally {
        this.clearAuth()
        ElMessage.success('已退出登录')
      }
    },

    clearAuth() {
      this.accessToken = null
      this.userInfo = null
      this.roles = []
      this.permissions = []
      this.expiresIn = 0
      localStorage.removeItem('auth-store')
    },

    setAccessToken(token) {
      this.accessToken = token
      this.persist()
    },

    persist() {
      const data = {
        accessToken: this.accessToken,
        userInfo: this.userInfo,
        roles: this.roles,
        permissions: this.permissions,
        expiresIn: this.expiresIn
      }
      localStorage.setItem('auth-store', JSON.stringify(data))
    },

    restore() {
      try {
        const data = localStorage.getItem('auth-store')
        if (data) {
          const parsed = JSON.parse(data)
          this.accessToken = parsed.accessToken
          this.userInfo = parsed.userInfo
          this.roles = parsed.roles || []
          this.permissions = parsed.permissions || []
          this.expiresIn = parsed.expiresIn || 0
        }
      } catch (error) {
        console.error('恢复认证信息失败:', error)
      }
    }
  },

  persist: {
    enabled: true,
    strategies: [{
      key: 'auth-store',
      storage: localStorage,
      paths: ['accessToken', 'userInfo', 'roles', 'permissions', 'expiresIn']
    }]
  }
})
