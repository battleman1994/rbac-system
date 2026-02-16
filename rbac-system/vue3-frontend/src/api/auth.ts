import request from '@/utils/request'
import type { ApiResponse, LoginResponse, User } from '@/types'

export interface LoginParams {
  username: string
  password: string
  captcha?: string
}

export const login = (params: LoginParams): Promise<ApiResponse<LoginResponse>> => {
  return request.post('/auth/login', params)
}

export const register = (params: LoginParams): Promise<ApiResponse<User>> => {
  return request.post('/auth/register', params)
}

export const logout = (): Promise<ApiResponse<void>> => {
  return request.post('/auth/logout')
}

export const refreshToken = (token: string): Promise<ApiResponse<LoginResponse>> => {
  return request.post('/auth/refresh', null, {
    headers: {
      'X-Refresh-Token': token
    }
  })
}

export const getCurrentUser = (): Promise<ApiResponse<User>> => {
  return request.get('/users/current')
}
