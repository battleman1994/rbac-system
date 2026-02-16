<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside :width="appStore.sidebarCollapsed ? '64px' : '210px'" class="sidebar">
        <div class="logo">
          <span v-if="!appStore.sidebarCollapsed">RBAC System</span>
          <span v-else>RB</span>
        </div>
        <SidebarMenu />
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-button
              type="text"
              :icon="appStore.sidebarCollapsed ? Expand : Fold"
              @click="appStore.toggleSidebar"
            />
            <breadcrumb />
          </div>
          <div class="header-right">
            <user-dropdown />
          </div>
        </el-header>
        
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { Expand, Fold } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import SidebarMenu from './components/SidebarMenu.vue'
import UserDropdown from './components/UserDropdown.vue'
import Breadcrumb from './components/Breadcrumb.vue'

const appStore = useAppStore()
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-right {
  display: flex;
  align-items: center;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
