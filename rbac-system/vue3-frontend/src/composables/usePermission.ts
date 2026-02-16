import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function usePermission() {
  const authStore = useAuthStore()
  
  const hasPermission = computed(() => {
    return (permission: string): boolean => {
      return authStore.hasPermission(permission)
    }
  })
  
  const hasAnyPermission = computed(() => {
    return (permissions: string[]): boolean => {
      return authStore.hasAnyPermission(permissions)
    }
  })
  
  const hasRole = computed(() => {
    return (role: string): boolean => {
      return authStore.hasRole(role)
    }
  })
  
  return {
    hasPermission,
    hasAnyPermission,
    hasRole
  }
}

export default usePermission
