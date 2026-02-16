<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>User Management</span>
          <el-button
            v-permission="'system:user:create'"
            type="primary"
            @click="handleAdd"
          >
            Add User
          </el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input
          v-model="searchForm.username"
          placeholder="Search by username"
          clearable
          style="width: 200px; margin-right: 10px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="Status"
          clearable
          style="width: 120px; margin-right: 10px"
        >
          <el-option label="Enabled" :value="1" />
          <el-option label="Disabled" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">Search</el-button>
        <el-button @click="resetSearch">Reset</el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="username" label="Username" min-width="120" />
        <el-table-column prop="email" label="Email" min-width="180" />
        <el-table-column prop="phone" label="Phone" min-width="130" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created At" min-width="160" />
        <el-table-column label="Actions" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:user:update'"
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              Edit
            </el-button>
            <el-button
              v-permission="'system:user:delete'"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <user-form-dialog
      v-model="dialogVisible"
      :type="dialogType"
      :data="currentRow"
      @success="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, deleteUser } from '@/api/user'
import type { User } from '@/types'
import UserFormDialog from './components/UserFormDialog.vue'

const loading = ref(false)
const tableData = ref<User[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const currentRow = ref<User | null>(null)

const searchForm = reactive({
  username: '',
  status: null as number | null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUsers({
      username: searchForm.username || undefined,
      status: searchForm.status ?? undefined,
      page: pagination.page - 1,
      size: pagination.size
    })
    tableData.value = res.data.content
    pagination.total = res.data.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const resetSearch = () => {
  searchForm.username = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogType.value = 'add'
  currentRow.value = null
  dialogVisible.value = true
}

const handleEdit = (row: User) => {
  dialogType.value = 'edit'
  currentRow.value = row
  dialogVisible.value = true
}

const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(`Delete user "${row.username}"?`, 'Confirm', {
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('Deleted successfully')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Delete failed')
    }
  }
}

const handleSizeChange = (val: number) => {
  pagination.size = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  pagination.page = val
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.user-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
