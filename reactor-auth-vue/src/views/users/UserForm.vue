<template>
  <el-dialog
    :model-value="visible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px"
    :close-on-click-modal="false" @update:model-value="$emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="姓名" prop="fullName">
        <el-input v-model="form.fullName" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="角色" prop="roles">
        <el-select v-model="form.roles" multiple placeholder="请选择角色" style="width: 100%">
          <el-option v-for="role in roleOptions" :key="role.code" :label="role.name" :value="role.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createUser, updateUser } from '@/api/user'
import { getRoles } from '@/api/role'
import { validators } from '@/utils/validator'

const props = defineProps({
  visible: { type: Boolean, default: false },
  user: { type: Object, default: null },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'success'])

const roleOptions = ref([])
onMounted(async () => {
  try { const res = await getRoles({ page: 1, size: 999 }); roleOptions.value = res?.records || res?.data || [] } catch (e) { /* ignore */ }
})

const formRef = ref()
const submitting = ref(false)
const form = reactive({ username: '', password: '', fullName: '', email: '', phone: '', roles: [], status: 1 })
const rules = {
  username: [{ validator: validators.username, trigger: 'blur' }],
  password: [{ validator: validators.password, trigger: 'blur' }],
  email: [{ validator: validators.email, trigger: 'blur' }],
  phone: [{ validator: validators.phone, trigger: 'blur' }]
}

watch(() => props.user, (user) => {
  Object.assign(form, {
    username: user?.username || '', password: '', fullName: user?.fullName || '',
    email: user?.email || '', phone: user?.phone || '', roles: user?.roles || [], status: user?.status ?? 1
  })
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.isEdit) {
        await updateUser(props.user.id, { ...form })
        ElMessage.success('更新成功')
      } else {
        await createUser({ ...form })
        ElMessage.success('创建成功')
      }
      emit('success')
    } catch (e) { ElMessage.error(e.message || '操作失败') }
    finally { submitting.value = false }
  })
}
</script>

<style scoped>
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
</style>
