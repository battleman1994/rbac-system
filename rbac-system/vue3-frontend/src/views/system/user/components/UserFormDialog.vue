<template>
  <el-dialog
    v-model="visible"
    :title="type === 'add' ? 'Add User' : 'Edit User'"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="Username" prop="username" v-if="type === 'add'">
        <el-input v-model="form.username" placeholder="Enter username" />
      </el-form-item>
      
      <el-form-item label="Password" prop="password" v-if="type === 'add'">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="Enter password"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="Email" prop="email">
        <el-input v-model="form.email" placeholder="Enter email" />
      </el-form-item>
      
      <el-form-item label="Phone" prop="phone">
        <el-input v-model="form.phone" placeholder="Enter phone" />
      </el-form-item>
      
      <el-form-item label="Status" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">Enabled</el-radio>
          <el-radio :label="0">Disabled</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="Roles" prop="roleIds">
        <el-select
          v-model="form.roleIds"
          multiple
          placeholder="Select roles"
          style="width: 100%"
        >
          <el-option
            v-for="role in roles"
            :key="role.id"
            :label="role.roleName"
            :value="role.id"
          />
        </el-select>
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
import { createUser, updateUser } from '@/api/user'
import { getRoles } from '@/api/role'
import type { User, Role } from '@/types'

interface Props {
  modelValue: boolean
  type: 'add' | 'edit'
  data: User | null
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formRef = ref<FormInstance>()
const submitting = ref(false)
const roles = ref<Role[]>([])

const form = reactive({
  username: '',
  password: '',
  email: '',
  phone: '',
  status: 1,
  roleIds: [] as number[]
})

const rules: FormRules = {
  username: [
    { required: true, message: 'Username is required', trigger: 'blur' },
    { min: 3, max: 50, message: '3-50 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 6, message: 'At least 6 characters', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: 'Invalid email format', trigger: 'blur' }
  ]
}

const loadRoles = async () => {
  try {
    const res = await getRoles()
    roles.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const resetForm = () => {
  form.username = ''
  form.password = ''
  form.email = ''
  form.phone = ''
  form.status = 1
  form.roleIds = []
  formRef.value?.resetFields()
}

const setFormData = () => {
  if (props.data) {
    form.email = props.data.email || ''
    form.phone = props.data.phone || ''
    form.status = props.data.status
    form.roleIds = [] // Would need to fetch from backend
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (props.type === 'add') {
          await createUser(form)
          ElMessage.success('User created successfully')
        } else {
          await updateUser(props.data!.id, {
            email: form.email,
            phone: form.phone,
            status: form.status,
            roleIds: form.roleIds
          })
          ElMessage.success('User updated successfully')
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
  if (val) {
    loadRoles()
    if (props.type === 'edit' && props.data) {
      setFormData()
    }
  }
})
</script>
