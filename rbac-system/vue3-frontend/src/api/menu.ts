import request from '@/utils/request'
import type { ApiResponse, Permission } from '@/types'

export const getMenus = (): Promise<ApiResponse<Permission[]>> => {
  return request.get('/menus')
}

export const getPermissions = (): Promise<ApiResponse<string[]>> => {
  return request.get('/permissions/current')
}

export const getAllPermissions = (): Promise<ApiResponse<Permission[]>> => {
  return request.get('/permissions')
}
