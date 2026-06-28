<template>
  <div class="menus-page">
    <el-card>
      <template #header><div class="page-header"><span>菜单管理（路由管理）</span><el-button type="primary" @click="handleAdd">新增菜单</el-button></div></template>
      <el-table v-loading="loading" :data="tree" row-key="id" default-expand-all :tree-props="{ children: 'children' }" stripe style="width: 100%">
        <el-table-column prop="title" label="菜单名称" min-width="160" />
        <el-table-column prop="name" label="路由名称" width="120" />
        <el-table-column prop="path" label="路由路径" width="160" />
        <el-table-column prop="redirect" label="重定向" width="140" show-overflow-tooltip>
          <template #default="{ row }"><span v-if="row.redirect">{{ row.redirect }}</span><span v-else>-</span></template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }"><el-icon v-if="row.icon" :size="18"><component :is="row.icon" /></el-icon><span v-else>-</span></template>
        </el-table-column>
        <el-table-column prop="component" label="组件路径" min-width="200" show-overflow-tooltip>
          <template #default="{ row }"><span v-if="row.component">{{ row.component }}</span><el-tag v-else size="small" type="info">目录</el-tag></template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }"><el-tag :type="typeTag(row.type)" size="small">{{ row.type }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="requiresAuth" label="需认证" width="80">
          <template #default="{ row }"><el-tag :type="row.requiresAuth !== false ? 'success' : 'warning'" size="small">{{ row.requiresAuth !== false ? '是' : '否' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column prop="hidden" label="隐藏" width="70">
          <template #default="{ row }"><el-tag :type="row.hidden ? 'warning' : 'info'" size="small">{{ row.hidden ? '是' : '否' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleAddChild(row)">新增子级</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <MenuForm v-model:visible="dialogVisible" :menu="currentMenu" :parent-id="currentParentId" :is-edit="isEdit" @success="onFormSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useMenuStore } from '@/store/modules/menu'
import { deleteMenu } from '@/api/menu'
import router from '@/router'
import { loadDynamicRoutes } from '@/router/dynamicRoutes'
import MenuForm from './MenuForm.vue'

const menuStore = useMenuStore()

const tree = ref([])
const loading = ref(false)
const dialogVisible = ref(false), currentMenu = ref(null), currentParentId = ref(null), isEdit = ref(false)

const fetchTree = async () => {
  loading.value = true
  try { tree.value = await menuStore.fetchMenuTree() } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

const handleAdd = () => { currentMenu.value = null; currentParentId.value = null; isEdit.value = false; dialogVisible.value = true }
const handleAddChild = (row) => { currentMenu.value = null; currentParentId.value = row.id; isEdit.value = false; dialogVisible.value = true }
const handleEdit = (row) => { currentMenu.value = { ...row }; currentParentId.value = row.parentId; isEdit.value = true; dialogVisible.value = true }

const handleDelete = (row) => {
  const msg = (row.children?.length) ? `确定要删除 "${row.title}" 及其所有子菜单吗？` : `确定要删除 "${row.title}" 吗？`
  ElMessageBox.confirm(msg, '删除确认', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
    .then(async () => {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      menuStore.setDynamicRoutesLoaded(false)
      await loadDynamicRoutes(router)
      fetchTree()
    }).catch(() => {})
}

const onFormSuccess = async () => { dialogVisible.value = false; await fetchTree() }
const typeTag = (t) => ({ MENU: '', BUTTON: 'warning', API: 'info' }[t] || '')
onMounted(() => fetchTree())
</script>

<style scoped>
.menus-page { padding: 0; }
.page-header { display: flex; align-items: center; justify-content: space-between; }
</style>
