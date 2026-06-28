<template>
  <div class="enums-page">
    <el-card class="section-card">
      <template #header><div class="page-header"><span>枚举类型</span><el-button type="primary" @click="handleAddType">新增类型</el-button></div></template>
      <el-table :data="typesWithValues" stripe highlight-current-row @row-click="selectType" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编码" min-width="150" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="值数量" width="80">
          <template #default="{ row }"><el-tag size="small" type="info">{{ row.values?.length || 0 }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click.stop="handleEditType(row)">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="handleDeleteType(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="section-card" v-if="selectedType">
      <template #header><div class="page-header"><span>枚举值 — {{ selectedType.name }}（{{ selectedType.code }}）</span><el-button type="primary" @click="handleAddValue">新增值</el-button></div></template>
      <el-table :data="selectedType.values" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编码" min-width="150" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditValue(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteValue(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-empty v-else description="请选择一个枚举类型" />

    <EnumTypeForm v-model:visible="typeVisible" :enum-type="currentType" :is-edit="isTypeEdit" @success="fetchData" />
    <EnumValueForm v-model:visible="valueVisible" :enum-value="currentValue" :type-id="selectedType?.id" :is-edit="isValueEdit" @success="fetchData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getEnumTypesWithValues, deleteEnumType } from '@/api/enum'
import { deleteEnumValue } from '@/api/enum'
import EnumTypeForm from './EnumTypeForm.vue'
import EnumValueForm from './EnumValueForm.vue'

const typesWithValues = ref([])
const selectedType = ref(null)

const typeVisible = ref(false), currentType = ref(null), isTypeEdit = ref(false)
const valueVisible = ref(false), currentValue = ref(null), isValueEdit = ref(false)

const fetchData = async () => {
  try {
    const res = await getEnumTypesWithValues()
    typesWithValues.value = Array.isArray(res) ? res : (res?.data || [])
    // Refresh selectedType from new data
    if (selectedType.value) {
      selectedType.value = typesWithValues.value.find(t => t.id === selectedType.value.id) || null
    }
  } catch (e) { ElMessage.error('获取枚举数据失败') }
}

const selectType = (row) => { selectedType.value = row }

const handleAddType = () => { currentType.value = null; isTypeEdit.value = false; typeVisible.value = true }
const handleEditType = (row) => { currentType.value = { ...row }; isTypeEdit.value = true; typeVisible.value = true }

const handleDeleteType = (row) => {
  ElMessageBox.confirm(`确定要删除枚举类型 "${row.name}" 及其所有枚举值吗？`, '删除确认', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
    .then(async () => {
      await deleteEnumType(row.id)
      ElMessage.success('删除成功')
      if (selectedType.value?.id === row.id) selectedType.value = null
      fetchData()
    }).catch(() => {})
}

const handleAddValue = () => { currentValue.value = null; isValueEdit.value = false; valueVisible.value = true }
const handleEditValue = (row) => { currentValue.value = { ...row }; isValueEdit.value = true; valueVisible.value = true }

const handleDeleteValue = (row) => {
  ElMessageBox.confirm(`确定要删除枚举值 "${row.name}" 吗？`, '删除确认', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
    .then(async () => { await deleteEnumValue(row.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
}

onMounted(() => fetchData())
</script>

<style scoped>
.enums-page { display: flex; flex-direction: column; gap: 16px; }
.section-card { width: 100%; }
.page-header { display: flex; align-items: center; justify-content: space-between; }
</style>
