<template>
  <el-dialog :model-value="visible" :title="isEdit ? '编辑枚举值' : '新增枚举值'" width="500px" :close-on-click-modal="false" @update:model-value="$emit('update:visible', $event)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="form.code" placeholder="如 MENU" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="如 菜单" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
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
import { createEnumValue, updateEnumValue } from '@/api/enum'

const props = defineProps({
  visible: { type: Boolean, default: false },
  enumValue: { type: Object, default: null },
  typeId: { type: [Number, String], default: null },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(), submitting = ref(false)
const form = reactive({ typeId: null, code: '', name: '', sortOrder: 0, status: 1 })
const rules = {
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

watch(() => props.enumValue, (v) => {
  Object.assign(form, {
    typeId: v?.typeId || props.typeId, code: v?.code || '', name: v?.name || '',
    sortOrder: v?.sortOrder ?? 0, status: v?.status ?? 1
  })
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.isEdit) { await updateEnumValue(props.enumValue.id, { ...form }); ElMessage.success('更新成功') }
      else { await createEnumValue({ ...form }); ElMessage.success('创建成功') }
      emit('success')
    } catch (e) { ElMessage.error(e.message || '操作失败') }
    finally { submitting.value = false }
  })
}
</script>
