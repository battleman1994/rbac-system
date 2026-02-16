<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <h3>Welcome, {{ authStore.userInfo?.username }}</h3>
          </template>
          <p>This is the RBAC Management System dashboard.</p>
          <p>Your roles: <el-tag v-for="role in authStore.userInfo?.roles" :key="role" class="role-tag">{{ role }}</el-tag></p>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <h4>System Overview</h4>
          </template>
          <div class="stats">
            <div class="stat-item">
              <div class="stat-value">{{ stats.users }}</div>
              <div class="stat-label">Users</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.roles }}</div>
              <div class="stat-label">Roles</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.permissions }}</div>
              <div class="stat-label">Permissions</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <h4>Your Permissions</h4>
          </template>
          <div class="permission-list">
            <el-tag 
              v-for="perm in authStore.userInfo?.permissions.slice(0, 10)" 
              :key="perm"
              size="small"
              class="permission-tag"
            >
              {{ perm }}
            </el-tag>
            <span v-if="(authStore.userInfo?.permissions.length || 0) > 10" class="more-tag">
              +{{ (authStore.userInfo?.permissions.length || 0) - 10 }} more
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getRoles } from '@/api/role'
import { getAllPermissions } from '@/api/menu'
import { getUsers } from '@/api/user'

const authStore = useAuthStore()

const stats = ref({
  users: 0,
  roles: 0,
  permissions: 0
})

onMounted(async () => {
  try {
    const [usersRes, rolesRes, permsRes] = await Promise.all([
      getUsers({ size: 1 }),
      getRoles(),
      getAllPermissions()
    ])
    stats.value.users = usersRes.data.totalElements || 0
    stats.value.roles = rolesRes.data.length || 0
    stats.value.permissions = permsRes.data.length || 0
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.role-tag {
  margin-right: 8px;
  margin-top: 8px;
}

.stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  margin-top: 8px;
  color: #606266;
}

.permission-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.permission-tag {
  margin: 0;
}

.more-tag {
  color: #909399;
  font-size: 12px;
  align-self: center;
}
</style>
