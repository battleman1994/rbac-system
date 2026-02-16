<template>
  <el-dialog
    v-model="visible"
    title="Assign Permissions"
    width="600px"
    @close="handleClose"
  >
    <el-tree
      ref="treeRef"
      :data="permissionTree"
      show-checkbox
      node-key="id"
      :default-expand-all="true"
      :props="{
        label: 'permissionName',
        children: 'children'
      }"
    />
    
    <template #footer>
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        Confirm
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { ElTree } from 'element-plus'
import { getAllPermissions } from '@/api/menu'
import { getRolePermissions, updateRolePermissions } from '@/api/role'
import type { Permission, Role } from '@/types'

interface Props {
  modelValue: boolean
  role: Role | null
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const treeRef = ref<InstanceType<typeof ElTree>>()
const submitting = ref(false)
const permissionTree = ref<Permission[]>([])
const checkedKeys = ref<number[]>([])

const loadPermissions = async () => {
  try {
    const res = await getAllPermissions()
    permissionTree.value = buildTree(res.data)
  } catch (error) {
    console.error(error)
  }
}

const loadRolePermissions = async () => {
  if (!props.role) return
  try {
    const res = await getRolePermissions(props.role.id)
    const permissionCodes = res.data
    
    const keys: number[] = []
    const findKeys = (items: Permission[]) => {
      items.forEach(item => {
        if (permissionCodes.includes(item.permissionCode)) {
          keys.push(item.id)
        }
        if (item.children) {
          findKeys(item.children)
        }
      })
    }
    findKeys(permissionTree.value)
    
    treeRef.value?.setCheckedKeys(keys)
  } catch (error) {
    console.error(error)
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

const handleSubmit = async () => {
  if (!props.role) return
  
  submitting.value = true
  try {
    const checkedKeys = treeRef.value?.getCheckedKeys() as number[]
    const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() as number[]
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    
    await updateRolePermissions(props.role.id, allKeys)
    ElMessage.success('Permissions updated successfully')
    visible.value = false
    emit('success')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || 'Update failed')
  } finally {
    submitting.value = false
  }
}

const handleClose = () => {
  treeRef.value?.setCheckedKeys([])
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    await loadPermissions()
    await loadRolePermissions()
  }
})
</script>
