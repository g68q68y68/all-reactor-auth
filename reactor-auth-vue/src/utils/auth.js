import { useAuthStore } from '@/store/modules/auth'

export const isAuthenticated = () => {
  const authStore = useAuthStore()
  return !!authStore.accessToken
}

export const hasRole = (role) => {
  const authStore = useAuthStore()
  return authStore.roles?.includes(role) || false
}

export const hasPermission = (permission) => {
  const authStore = useAuthStore()
  return authStore.permissions?.includes(permission) || false
}

export const hasAnyRole = (roles) => {
  return roles.some(role => hasRole(role))
}

export const hasAnyPermission = (permissions) => {
  return permissions.some(permission => hasPermission(permission))
}
