import request from '@/utils/request'
import type { ApiResponse, User } from '@/types'

export interface UserQueryParams {
  username?: string
  status?: number
  page?: number
  size?: number
}

export interface CreateUserParams {
  username: string
  password: string
  email?: string
  phone?: string
  avatar?: string
  status?: number
  roleIds?: number[]
}

export interface UpdateUserParams {
  email?: string
  phone?: string
  avatar?: string
  status?: number
  roleIds?: number[]
}

export const getUsers = (params?: UserQueryParams): Promise<ApiResponse<any>> => {
  return request.get('/users', { params })
}

export const getUserById = (id: number): Promise<ApiResponse<User>> => {
  return request.get(`/users/${id}`)
}

export const createUser = (params: CreateUserParams): Promise<ApiResponse<User>> => {
  return request.post('/users', params)
}

export const updateUser = (id: number, params: UpdateUserParams): Promise<ApiResponse<User>> => {
  return request.put(`/users/${id}`, params)
}

export const deleteUser = (id: number): Promise<ApiResponse<void>> => {
  return request.delete(`/users/${id}`)
}

export const resetPassword = (id: number, newPassword: string): Promise<ApiResponse<void>> => {
  return request.put(`/users/${id}/password`, null, {
    params: { newPassword }
  })
}
