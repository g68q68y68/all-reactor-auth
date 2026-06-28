<template>
  <el-dialog :model-value="visible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" :close-on-click-modal="false" @update:model-value="$emit('update:visible', $event)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="角色编码" prop="code">
        <el-input v-model="form.code" placeholder="如 ROLE_ADMIN" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" placeholder="如 管理员" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" placeholder="选填" />
      </el-form-item>
      <el-form-item label="状态">
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
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createRole, updateRole } from '@/api/role'

const props = defineProps({
  visible: { type: Boolean, default: false },
  role: { type: Object, default: null },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(), submitting = ref(false)
const form = reactive({ code: '', name: '', description: '', status: 1 })
const rules = {
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }, { pattern: /^[A-Z_]+$/, message: '大写字母和下划线', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

watch(() => props.role, (role) => {
  Object.assign(form, { code: role?.code || '', name: role?.name || '', description: role?.description || '', status: role?.status ?? 1 })
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.isEdit) { await updateRole(props.role.id, { ...form }); ElMessage.success('更新成功') }
      else { await createRole({ ...form }); ElMessage.success('创建成功') }
      emit('success')
    } catch (e) { ElMessage.error(e.message || '操作失败') }
    finally { submitting.value = false }
  })
}
</script>
