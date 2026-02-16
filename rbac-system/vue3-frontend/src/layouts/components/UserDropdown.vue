<template>
  <el-dropdown @command="handleCommand">
    <span class="user-info">
      <el-avatar :size="32" :src="authStore.userInfo?.avatar || defaultAvatar" />
      <span class="username">{{ authStore.userInfo?.username }}</span>
      <el-icon class="el-icon--right"><arrow-down /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="profile">Profile</el-dropdown-item>
        <el-dropdown-item command="settings">Settings</el-dropdown-item>
        <el-dropdown-item divided command="logout">Logout</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('Are you sure you want to logout?', 'Logout', {
    confirmButtonText: 'Yes',
    cancelButtonText: 'No',
    type: 'warning'
  }).then(() => {
    authStore.logout()
    ElMessage.success('Logged out successfully')
    router.push('/login')
  })
}
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}
</style>
