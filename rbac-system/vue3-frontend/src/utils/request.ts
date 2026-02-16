import type { AxiosInstance, AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getToken } from '@/utils/token'
import router from '@/router'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request: AxiosInstance = axios.create({
  baseURL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

const subscribeTokenRefresh = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback)
}

const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach(callback => callback(token))
  refreshSubscribers = []
}

request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message, data } = response.data
    
    if (code === 200) {
      return { ...response, data }
    } else {
      ElMessage.error(message || 'Request failed')
      return Promise.reject(new Error(message || 'Request failed'))
    }
  },
  async (error: AxiosError) => {
    const { response, config } = error
    
    if (response?.status === 401) {
      const authStore = useAuthStore()
      
      if (!isRefreshing) {
        isRefreshing = true
        try {
          const newToken = await authStore.refreshAccessToken()
          isRefreshing = false
          onTokenRefreshed(newToken)
          
          if (config && config.headers) {
            config.headers.Authorization = `Bearer ${newToken}`
          }
          return request(config!)
        } catch (refreshError) {
          isRefreshing = false
          refreshSubscribers = []
          authStore.logout()
          router.push('/login')
          ElMessage.error('Session expired. Please login again.')
          return Promise.reject(refreshError)
        }
      } else {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            if (config && config.headers) {
              config.headers.Authorization = `Bearer ${token}`
            }
            resolve(request(config!))
          })
        })
      }
    }
    
    if (response?.status === 403) {
      ElMessage.error('Access denied. Insufficient permissions.')
    } else if (response?.status === 500) {
      ElMessage.error('Server error. Please try again later.')
    } else {
      const message = (response?.data as any)?.message || 'Network error'
      ElMessage.error(message)
    }
    
    return Promise.reject(error)
  }
)

export default request
