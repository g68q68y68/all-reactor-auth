<template>
  <div class="roles-page">
    <el-card>
      <template #header><div class="page-header"><span>角色管理</span><el-button type="primary" @click="handleAdd">新增角色</el-button></div></template>
      <el-table v-loading="loading" :data="list" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编码" min-width="180" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="warning" @click="handleMenuConfig(row)">菜单权限</el-button>
            <el-button size="small" type="success" @click="handlePermConfig(row)">权限配置</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <AppPagination v-model:page="page" v-model:size="size" :total="total" @change="fetchData" />
    </el-card>
    <RoleForm v-model:visible="dialogVisible" :role="current" :is-edit="isEdit" @success="onFormSuccess" />
    <RoleMenuDialog v-model:visible="menuVisible" :role="current" />
    <RolePermissionDialog v-model:visible="permVisible" :role="current" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getRoles, deleteRole } from '@/api/role'
import { usePagination } from '@/composables/usePagination'
import AppPagination from '@/components/common/AppPagination.vue'
import RoleForm from './RoleForm.vue'
import RoleMenuDialog from './RoleMenuDialog.vue'
import RolePermissionDialog from './RolePermissionDialog.vue'

const { list, total, loading, page, size, fetchData } = usePagination(getRoles)
const dialogVisible = ref(false), menuVisible = ref(false), permVisible = ref(false), current = ref(null), isEdit = ref(false)

const handleAdd = () => { current.value = null; isEdit.value = false; dialogVisible.value = true }
const handleEdit = (row) => { current.value = { ...row }; isEdit.value = true; dialogVisible.value = true }
const handleMenuConfig = (row) => { current.value = { ...row }; menuVisible.value = true }
const handlePermConfig = (row) => { current.value = { ...row }; permVisible.value = true }
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色 "${row.name}" 吗？`, '删除确认', { confirmButtonText:'确定删除', cancelButtonText:'取消', type:'warning' })
    .then(async () => { await deleteRole(row.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
}
const onFormSuccess = () => { dialogVisible.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.roles-page { padding:0; }
.page-header { display:flex; align-items:center; justify-content:space-between; }
</style>
