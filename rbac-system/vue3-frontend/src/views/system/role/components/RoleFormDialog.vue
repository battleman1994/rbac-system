<template>
  <el-dialog
    v-model="visible"
    :title="type === 'add' ? 'Add Role' : 'Edit Role'"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="Role Name" prop="roleName">
        <el-input v-model="form.roleName" placeholder="Enter role name" />
      </el-form-item>
      
      <el-form-item label="Role Code" prop="roleCode">
        <el-input v-model="form.roleCode" placeholder="Enter role code (e.g., ROLE_ADMIN)" />
      </el-form-item>
      
      <el-form-item label="Description" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="Enter description"
        />
      </el-form-item>
      
      <el-form-item label="Status" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">Enabled</el-radio>
          <el-radio :label="0">Disabled</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        Confirm
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createRole, updateRole } from '@/api/role'
import type { Role } from '@/types'

interface Props {
  modelValue: boolean
  type: 'add' | 'edit'
  data: Role | null
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules: FormRules = {
  roleName: [
    { required: true, message: 'Role name is required', trigger: 'blur' },
    { max: 50, message: 'Max 50 characters', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: 'Role code is required', trigger: 'blur' },
    { max: 50, message: 'Max 50 characters', trigger: 'blur' },
    { pattern: /^ROLE_[A-Z_]+$/, message: 'Format: ROLE_XXX', trigger: 'blur' }
  ]
}

const resetForm = () => {
  form.roleName = ''
  form.roleCode = ''
  form.description = ''
  form.status = 1
  formRef.value?.resetFields()
}

const setFormData = () => {
  if (props.data) {
    form.roleName = props.data.roleName
    form.roleCode = props.data.roleCode
    form.description = props.data.description || ''
    form.status = props.data.status
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (props.type === 'add') {
          await createRole(form)
          ElMessage.success('Role created successfully')
        } else {
          await updateRole(props.data!.id, form)
          ElMessage.success('Role updated successfully')
        }
        visible.value = false
        emit('success')
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Operation failed')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleClose = () => {
  resetForm()
}

watch(() => props.modelValue, (val) => {
  if (val && props.type === 'edit' && props.data) {
    setFormData()
  }
})
</script>
