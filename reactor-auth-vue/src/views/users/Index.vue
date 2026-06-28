<template>
  <div class="users-page">
    <el-card>
      <template #header>
        <div class="page-header"><span>用户管理</span><el-button type="primary" @click="handleAdd">新增用户</el-button></div>
      </template>
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="fullName" label="姓名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role" size="small" style="margin-right:4px" :type="role==='ROLE_ADMIN'?'danger':'info'">{{ role==='ROLE_ADMIN'?'管理员':'用户' }}</el-tag>
          </template>
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
    <UserForm v-model:visible="dialogVisible" :user="current" :is-edit="isEdit" @success="onFormSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getUsers, deleteUser } from '@/api/user'
import { usePagination } from '@/composables/usePagination'
import AppPagination from '@/components/common/AppPagination.vue'
import UserForm from './UserForm.vue'

const { list, total, loading, page, size, fetchData } = usePagination(getUsers)
const dialogVisible = ref(false), current = ref(null), isEdit = ref(false)

const handleAdd = () => { current.value = null; isEdit.value = false; dialogVisible.value = true }
const handleEdit = (row) => { current.value = { ...row }; isEdit.value = true; dialogVisible.value = true }
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '删除确认', { confirmButtonText:'确定删除', cancelButtonText:'取消', type:'warning' })
    .then(async () => { await deleteUser(row.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
}
const onFormSuccess = () => { dialogVisible.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.users-page { padding:0; }
.page-header { display:flex; align-items:center; justify-content:space-between; }
</style>
