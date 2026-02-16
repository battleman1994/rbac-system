import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Permission } from '@/types'
import { getMenus, getPermissions } from '@/api/menu'
import { useAuthStore } from './auth'

export const usePermissionStore = defineStore('permission', () => {
  const menus = ref<Permission[]>([])
  const permissions = ref<string[]>([])
  const cachedRoutes = ref<string[]>([])
  const loading = ref(false)

  const sidebarMenus = computed(() => {
    return menus.value.filter(menu => menu.type === 'menu' && !menu.parentId)
  })

  const hasFetched = computed(() => menus.value.length > 0)

  const fetchUserMenus = async () => {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) return
    
    loading.value = true
    try {
      const response = await getMenus()
      menus.value = buildMenuTree(response.data)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const fetchUserPermissions = async () => {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) return
    
    try {
      const response = await getPermissions()
      permissions.value = response.data
      return response.data
    } catch (error) {
      console.error('Failed to fetch permissions:', error)
    }
  }

  const buildMenuTree = (menuList: Permission[]): Permission[] => {
    const menuMap = new Map<number, Permission>()
    const tree: Permission[] = []

    menuList.forEach(menu => {
      menuMap.set(menu.id, { ...menu, children: [] })
    })

    menuList.forEach(menu => {
      const menuItem = menuMap.get(menu.id)!
      if (menu.parentId && menuMap.has(menu.parentId)) {
        const parent = menuMap.get(menu.parentId)!
        if (!parent.children) parent.children = []
        parent.children.push(menuItem)
      } else {
        tree.push(menuItem)
      }
    })

    return tree.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  }

  const addCachedRoute = (route: string) => {
    if (!cachedRoutes.value.includes(route)) {
      cachedRoutes.value.push(route)
    }
  }

  const removeCachedRoute = (route: string) => {
    const index = cachedRoutes.value.indexOf(route)
    if (index > -1) {
      cachedRoutes.value.splice(index, 1)
    }
  }

  const clearPermission = () => {
    menus.value = []
    permissions.value = []
    cachedRoutes.value = []
  }

  return {
    menus,
    permissions,
    cachedRoutes,
    loading,
    sidebarMenus,
    hasFetched,
    fetchUserMenus,
    fetchUserPermissions,
    buildMenuTree,
    addCachedRoute,
    removeCachedRoute,
    clearPermission
  }
})
