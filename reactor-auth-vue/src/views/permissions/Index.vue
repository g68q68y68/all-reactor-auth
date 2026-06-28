<template>
  <div class="permissions-page">
    <el-card>
      <template #header><div class="page-header"><span>权限管理</span><el-button type="primary" @click="handleAdd">新增权限</el-button></div></template>
      <el-table v-loading="loading" :data="list" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编码" min-width="180" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }"><el-tag :type="typeTag(row.type)" size="small">{{ row.type }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="url" label="路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" width="100">
          <template #default="{ row }"><el-tag v-if="row.method" :type="methodTag(row.method)" size="small">{{ row.method }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <AppPagination v-model:page="page" v-model:size="size" :total="total" @change="fetchData" />
    </el-card>
    <PermissionForm v-model:visible="dialogVisible" :permission="current" :is-edit="isEdit" @success="onFormSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getPermissions, deletePermission } from '@/api/permission'
import { usePagination } from '@/composables/usePagination'
import AppPagination from '@/components/common/AppPagination.vue'
import PermissionForm from './PermissionForm.vue'

const { list, total, loading, page, size, fetchData } = usePagination(getPermissions)
const dialogVisible = ref(false), current = ref(null), isEdit = ref(false)

const handleAdd = () => { current.value = null; isEdit.value = false; dialogVisible.value = true }
const handleEdit = (r) => { current.value = { ...r }; isEdit.value = true; dialogVisible.value = true }
const handleDelete = (r) => {
  ElMessageBox.confirm(`确定要删除权限 "${r.name}" 吗？`, '删除确认', { confirmButtonText:'确定删除', cancelButtonText:'取消', type:'warning' })
    .then(async () => { await deletePermission(r.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
}
const onFormSuccess = () => { dialogVisible.value = false; fetchData() }
const typeTag = (t) => ({ MENU:'', BUTTON:'warning', API:'info' }[t]||'')
const methodTag = (m) => ({ GET:'success', POST:'primary', PUT:'warning', DELETE:'danger' }[m]||'')
onMounted(() => fetchData())
</script>

<style scoped>
.permissions-page { padding:0; }
.page-header { display:flex; align-items:center; justify-content:space-between; }
</style>
