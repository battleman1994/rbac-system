import type { Directive, DirectiveBinding } from 'vue'
import { useAuthStore } from '@/stores/auth'

const checkPermission = (el: HTMLElement, binding: DirectiveBinding<string | string[]>) => {
  const authStore = useAuthStore()
  const { value } = binding
  
  if (Array.isArray(value)) {
    if (!authStore.hasAnyPermission(value)) {
      el.parentNode?.removeChild(el)
    }
  } else {
    if (!authStore.hasPermission(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}

export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding)
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding)
  }
}

export default permission
