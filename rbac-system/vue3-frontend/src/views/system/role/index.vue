<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Role Management</span>
          <el-button
            v-permission="'system:role:create'"
            type="primary"
            @click="handleAdd"
          >
            Add Role
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="roleName" label="Role Name" min-width="150" />
        <el-table-column prop="roleCode" label="Role Code" min-width="150" />
        <el-table-column prop="description" label="Description" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:role:update'"
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              Edit
            </el-button>
            <el-button
              v-permission="'system:role:permission'"
              type="warning"
              size="small"
              @click="handlePermission(row)"
            >
              Permissions
            </el-button>
            <el-button
              v-permission="'system:role:delete'"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <role-form-dialog
      v-model="dialogVisible"
      :type="dialogType"
      :data="currentRow"
      @success="loadData"
    />
    
    <permission-dialog
      v-model="permissionDialogVisible"
      :role="currentRow"
      @success="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoles, deleteRole } from '@/api/role'
import type { Role } from '@/types'
import RoleFormDialog from './components/RoleFormDialog.vue'
import PermissionDialog from './components/PermissionDialog.vue'

const loading = ref(false)
const tableData = ref<Role[]>([])
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const currentRow = ref<Role | null>(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoles()
    tableData.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogType.value = 'add'
  currentRow.value = null
  dialogVisible.value = true
}

const handleEdit = (row: Role) => {
  dialogType.value = 'edit'
  currentRow.value = row
  dialogVisible.value = true
}

const handlePermission = (row: Role) => {
  currentRow.value = row
  permissionDialogVisible.value = true
}

const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(`Delete role "${row.roleName}"?`, 'Confirm', {
      type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('Deleted successfully')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Delete failed')
    }
  }
}

onMounted(loadData)
</script>

<style scoped>
.role-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
