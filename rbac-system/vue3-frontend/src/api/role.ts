import request from '@/utils/request'
import type { ApiResponse, Role } from '@/types'

export interface CreateRoleParams {
  roleName: string
  roleCode: string
  description?: string
  status?: number
  permissionIds?: number[]
}

export const getRoles = (): Promise<ApiResponse<Role[]>> => {
  return request.get('/roles')
}

export const getRoleById = (id: number): Promise<ApiResponse<Role>> => {
  return request.get(`/roles/${id}`)
}

export const createRole = (params: CreateRoleParams): Promise<ApiResponse<Role>> => {
  return request.post('/roles', params)
}

export const updateRole = (id: number, params: CreateRoleParams): Promise<ApiResponse<Role>> => {
  return request.put(`/roles/${id}`, params)
}

export const deleteRole = (id: number): Promise<ApiResponse<void>> => {
  return request.delete(`/roles/${id}`)
}

export const getRolePermissions = (id: number): Promise<ApiResponse<string[]>> => {
  return request.get(`/roles/${id}/permissions`)
}

export const updateRolePermissions = (id: number, permissionIds: number[]): Promise<ApiResponse<void>> => {
  return request.put(`/roles/${id}/permissions`, permissionIds)
}
