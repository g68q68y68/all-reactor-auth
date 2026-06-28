<template>
  <el-dialog
    :model-value="visible"
    :title="`菜单权限 — ${role?.name || ''}`"
    width="520px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
    @open="loadData"
  >
    <el-tree
      ref="treeRef"
      :data="menuTree"
      :props="{ label: 'title', children: 'children' }"
      node-key="id"
      show-checkbox
      default-expand-all
      :check-strictly="false"
      style="max-height: 450px; overflow-y: auto"
    />

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { getMenuTree } from '@/api/menu'
import { getRoleMenus, assignRoleMenus } from '@/api/role'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  role: { type: Object, default: null }
})

const emit = defineEmits(['update:visible', 'success'])
const treeRef = ref(null)
const saving = ref(false)
const menuTree = ref([])

const loadData = async () => {
  if (!props.role) return
  try {
    // 拉取全部菜单树和角色已分配的菜单 ID
    const [tree, menuIds] = await Promise.all([
      getMenuTree(),
      getRoleMenus(props.role.id)
    ])
    menuTree.value = tree || []
    // 等待 DOM 更新后设置选中
    await new Promise(r => setTimeout(r, 0))
    if (treeRef.value && menuIds) {
      treeRef.value.setCheckedKeys(menuIds)
    }
  } catch (error) {
    ElMessage.error('加载菜单数据失败')
  }
}

const handleSave = async () => {
  if (!props.role || !treeRef.value) return
  saving.value = true
  try {
    // 获取所有选中节点（含半选父节点）
    const checkedKeys = treeRef.value.getCheckedKeys()
    const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]

    await assignRoleMenus(props.role.id, allKeys)
    ElMessage.success('菜单权限保存成功')
    emit('success')
    emit('update:visible', false)
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}
</script>
