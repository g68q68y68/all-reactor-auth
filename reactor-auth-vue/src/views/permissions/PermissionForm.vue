<template>
  <el-dialog :model-value="visible" :title="isEdit ? '编辑权限' : '新增权限'" width="500px" :close-on-click-modal="false" @update:model-value="$emit('update:visible', $event)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="权限编码" prop="code">
        <el-input v-model="form.code" placeholder="如 user:read" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="权限名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入权限名称" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type" style="width: 100%">
          <el-option label="菜单 (MENU)" value="MENU" /><el-option label="按钮 (BUTTON)" value="BUTTON" /><el-option label="接口 (API)" value="API" />
        </el-select>
      </el-form-item>
      <el-form-item label="路径" prop="url">
        <el-input v-model="form.url" placeholder="如 /api/users" />
      </el-form-item>
      <el-form-item label="请求方法">
        <el-select v-model="form.method" placeholder="请选择" style="width: 100%" clearable>
          <el-option label="GET" value="GET" /><el-option label="POST" value="POST" /><el-option label="PUT" value="PUT" /><el-option label="DELETE" value="DELETE" />
        </el-select>
      </el-form-item>
      <el-form-item label="刷新缓存">
        <el-switch v-model="form.refreshCache" active-text="是" inactive-text="否" />
        <span class="form-tip">开启后更新 Redis 中的权限缓存</span>
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
import { createPermission, updatePermission, refreshPermissionCache } from '@/api/permission'

const props = defineProps({
  visible: { type: Boolean, default: false },
  permission: { type: Object, default: null },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(), submitting = ref(false)
const form = reactive({ code: '', name: '', type: 'API', url: '', method: 'GET', refreshCache: false, status: 1 })
const rules = {
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }, { pattern: /^[a-z]+(:[a-z]+)+$/, message: '格式 resource:action', trigger: 'blur' }],
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }]
}

watch(() => props.permission, (p) => {
  Object.assign(form, { code: p?.code || '', name: p?.name || '', type: p?.type || 'API', url: p?.url || '', method: p?.method || 'GET', status: p?.status ?? 1 })
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.isEdit) { await updatePermission(props.permission.id, { ...form }); ElMessage.success('更新成功') }
      else { await createPermission({ ...form }); ElMessage.success('创建成功') }
      if (form.refreshCache) { await refreshPermissionCache().catch(() => {}) }
      emit('success')
    } catch (e) { ElMessage.error(e.message || '操作失败') }
    finally { submitting.value = false }
  })
}
</script>

<style scoped>
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.form-tip { font-size: 12px; color: #909399; margin-left: 8px; }
</style>
