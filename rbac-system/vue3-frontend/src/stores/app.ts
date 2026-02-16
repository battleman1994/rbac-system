import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const theme = ref<'light' | 'dark'>('light')
  const language = ref('en')

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  const setTheme = (newTheme: 'light' | 'dark') => {
    theme.value = newTheme
  }

  const setLanguage = (lang: string) => {
    language.value = lang
  }

  return {
    sidebarCollapsed,
    theme,
    language,
    toggleSidebar,
    setTheme,
    setLanguage
  }
})
