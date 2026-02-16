<template>
  <el-menu
    :default-active="$route.path"
    :collapse="appStore.sidebarCollapsed"
    :collapse-transition="false"
    router
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
  >
    <template v-for="route in permissionStore.sidebarMenus" :key="route.path">
      <el-sub-menu v-if="route.children && route.children.length" :index="route.path">
        <template #title>
          <el-icon v-if="route.icon">
            <component :is="getIcon(route.icon)" />
          </el-icon>
          <span>{{ route.permissionName }}</span>
        </template>
        <el-menu-item
          v-for="child in route.children"
          :key="child.path"
          :index="child.path"
        >
          {{ child.permissionName }}
        </el-menu-item>
      </el-sub-menu>
      
      <el-menu-item v-else :index="route.path">
        <el-icon v-if="route.icon">
          <component :is="getIcon(route.icon)" />
        </el-icon>
        <template #title>{{ route.permissionName }}</template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { usePermissionStore } from '@/stores/permission'
import * as Icons from '@element-plus/icons-vue'

const appStore = useAppStore()
const permissionStore = usePermissionStore()

const getIcon = (iconName: string) => {
  return (Icons as any)[iconName] || 'Menu'
}

onMounted(() => {
  if (!permissionStore.hasFetched) {
    permissionStore.fetchUserMenus()
  }
})
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>
