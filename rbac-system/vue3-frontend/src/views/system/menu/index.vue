<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Menu Management</span>
          <el-button
            v-permission="'system:menu:create'"
            type="primary"
            @click="handleAdd"
          >
            Add Menu
          </el-button>
        </div>
      </template>
      
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        row-key="id"
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="permissionName" label="Name" min-width="180">
          <template #default="{ row }">
            <el-icon v-if="row.icon" style="margin-right: 8px">
              <component :is="getIcon(row.icon)" />
            </el-icon>
            {{ row.permissionName }}
          </template>
        </el-table-column>
        <el-table-column prop="permissionCode" label="Code" min-width="150" />
        <el-table-column prop="type" label="Type" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'menu' ? 'primary' : 'warning'">
              {{ row.type === 'menu' ? 'Menu' : 'Button' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="Path" min-width="150" />
        <el-table-column prop="sortOrder" label="Sort" width="80" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:menu:update'"
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              Edit
            </el-button>
            <el-button
              v-permission="'system:menu:delete'"
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllPermissions } from '@/api/menu'
import type { Permission } from '@/types'
import * as Icons from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref<Permission[]>([])

const getIcon = (iconName: string) => {
  return (Icons as any)[iconName] || 'Menu'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllPermissions()
    tableData.value = buildTree(res.data)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const buildTree = (items: Permission[]): Permission[] => {
  const map = new Map<number, Permission>()
  const tree: Permission[] = []
  
  items.forEach(item => {
    map.set(item.id, { ...item, children: [] })
  })
  
  items.forEach(item => {
    const node = map.get(item.id)!
    if (item.parentId && map.has(item.parentId)) {
      const parent = map.get(item.parentId)!
      if (!parent.children) parent.children = []
      parent.children.push(node)
    } else {
      tree.push(node)
    }
  })
  
  return tree.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
}

const handleAdd = () => {
  ElMessage.info('Menu creation to be implemented')
}

const handleEdit = (row: Permission) => {
  console.log('Edit', row)
}

const handleDelete = (row: Permission) => {
  console.log('Delete', row)
}

onMounted(loadData)
</script>

<style scoped>
.menu-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
