export interface UserInfo {
  id: number
  username: string
  email?: string
  avatar?: string
  roles: string[]
  permissions: string[]
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: UserInfo
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface User {
  id: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  status: number
  createdAt?: string
  lastLoginAt?: string
  roles: string[]
  permissions: string[]
}

export interface Role {
  id: number
  roleName: string
  roleCode: string
  description?: string
  status: number
  createdAt?: string
  permissions: string[]
}

export interface Permission {
  id: number
  permissionName: string
  permissionCode: string
  type: 'menu' | 'button'
  parentId?: number
  sortOrder?: number
  icon?: string
  path?: string
  component?: string
  status: number
  children?: Permission[]
}
