<template>
  <el-dialog
    :model-value="visible"
    :title="`权限配置 — ${role?.name || ''}`"
    width="750px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
    @open="loadData"
  >
    <el-table
      ref="tableRef"
      :data="permissions"
      row-key="id"
      @selection-change="onSelectionChange"
      style="width: 100%"
      max-height="460"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="code" label="编码" min-width="160" />
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="type" label="类型" width="70">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.type)" size="small">{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="url" label="路径" min-width="220" show-overflow-tooltip />
      <el-table-column prop="method" label="方法" width="70">
        <template #default="{ row }">
          <el-tag v-if="row.method" :type="methodTag(row.method)" size="small">{{ row.method }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { getPermissions } from '@/api/permission'
import { getRolePermissions, assignRolePermissions } from '@/api/role'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  role: { type: Object, default: null }
})
const emit = defineEmits(['update:visible', 'success'])

const tableRef = ref(null)
const saving = ref(false)
const permissions = ref([])
const selected = ref([])

const loadData = async () => {
  if (!props.role) return
  try {
    const [allPerms, rolePermIds] = await Promise.all([
      getPermissions({ page: 0, size: 999 }),
      getRolePermissions(props.role.id)
    ])
    permissions.value = allPerms?.records || allPerms?.data || []
    await nextTick()
    // 回显已分配的权限
    if (rolePermIds?.length) {
      permissions.value.forEach(row => {
        if (rolePermIds.includes(row.id)) {
          tableRef.value?.toggleRowSelection(row, true)
        }
      })
    }
  } catch (e) {
    ElMessage.error('加载权限数据失败')
  }
}

const onSelectionChange = (val) => {
  selected.value = val
}

const handleSave = async () => {
  if (!props.role) return
  saving.value = true
  try {
    const ids = selected.value.map(r => r.id)
    await assignRolePermissions(props.role.id, ids)
    ElMessage.success('权限配置保存成功')
    emit('success')
    emit('update:visible', false)
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const typeTag = (t) => ({ MENU: '', BUTTON: 'warning', API: 'info' }[t] || '')
const methodTag = (m) => ({ GET: 'success', POST: 'primary', PUT: 'warning', DELETE: 'danger' }[m] || '')
</script>
