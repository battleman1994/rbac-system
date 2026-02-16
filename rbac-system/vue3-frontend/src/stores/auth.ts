import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginResponse } from '@/types'
import { login as loginApi, getCurrentUser, refreshToken as refreshTokenApi } from '@/api/auth'
import { setToken, getToken, removeToken, setRefreshToken, getRefreshToken, removeRefreshToken } from '@/utils/token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(getToken() || '')
  const refreshTokenValue = ref<string>(getRefreshToken() || '')
  const userInfo = ref<UserInfo | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)
  const hasRole = computed(() => (role: string) => userInfo.value?.roles.includes(role) || false)
  const hasPermission = computed(() => (permission: string) => {
    if (!userInfo.value?.permissions) return false
    const permissions = userInfo.value.permissions
    return permissions.includes(permission) || permissions.includes('*:*:*')
  })
  const hasAnyPermission = computed(() => (permissions: string[]) => {
    return permissions.some(p => hasPermission.value(p))
  })

  const login = async (username: string, password: string, captcha?: string) => {
    loading.value = true
    try {
      const response = await loginApi({ username, password, captcha })
      const { accessToken, refreshToken: refreshTokenVal, user } = response.data
      
      token.value = accessToken
      refreshTokenValue.value = refreshTokenVal
      userInfo.value = user
      
      setToken(accessToken)
      setRefreshToken(refreshTokenVal)
      
      return response
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    token.value = ''
    refreshTokenValue.value = ''
    userInfo.value = null
    removeToken()
    removeRefreshToken()
  }

  const fetchUserInfo = async () => {
    try {
      const response = await getCurrentUser()
      userInfo.value = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email,
        avatar: response.data.avatar,
        roles: response.data.roles,
        permissions: response.data.permissions
      }
      return response.data
    } catch (error) {
      logout()
      throw error
    }
  }

  const refreshAccessToken = async () => {
    try {
      const currentRefreshToken = getRefreshToken()
      if (!currentRefreshToken) {
        throw new Error('No refresh token available')
      }
      
      const response = await refreshTokenApi(currentRefreshToken)
      const { accessToken, refreshToken: refreshTokenVal } = response.data
      
      token.value = accessToken
      refreshTokenValue.value = refreshTokenVal
      
      setToken(accessToken)
      setRefreshToken(refreshTokenVal)
      
      return accessToken
    } catch (error) {
      logout()
      throw error
    }
  }

  const setAccessToken = (accessToken: string) => {
    token.value = accessToken
    setToken(accessToken)
  }

  return {
    token,
    refreshTokenValue,
    userInfo,
    loading,
    isLoggedIn,
    hasRole,
    hasPermission,
    hasAnyPermission,
    login,
    logout,
    fetchUserInfo,
    refreshAccessToken,
    setAccessToken
  }
})
