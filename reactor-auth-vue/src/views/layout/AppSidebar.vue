<template>
  <el-aside width="200px" class="app-sidebar">
    <div class="logo">
      <el-icon :size="28"><DataAnalysis /></el-icon>
      <span>{{ appTitle }}</span>
    </div>

    <el-menu
      router
      :default-active="activeMenu"
      class="sidebar-menu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <SidebarMenuItem
        v-for="item in rootItems"
        :key="item.id"
        :item="item"
        parent-path=""
      />
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useMenuStore } from '@/store/modules/menu'
import { DataAnalysis } from '@element-plus/icons-vue'
import SidebarMenuItem from './SidebarMenuItem.vue'

defineProps({ appTitle: { type: String, default: '' } })

const route = useRoute()
const menuStore = useMenuStore()

const activeMenu = computed(() => route.path)

// 根节点：跳过 hidden，子节点提升并继承 hidden 节点的路径
const rootItems = computed(() => {
  const result = []
  for (const node of menuStore.menuTree) {
    if (node.status !== 1 || node.type !== 'MENU') continue
    if (node.hidden) {
      const hp = node.path || ''
      for (const child of node.children || []) {
        if (child.status === 1 && child.type === 'MENU' && !child.hidden) {
          result.push({ ...child, _inheritedPath: hp })
        }
      }
    } else {
      result.push(node)
    }
  }
  return result
})
</script>

<style scoped>
.app-sidebar { background-color: #304156; overflow-y: auto; }
.logo {
  height: 60px; display: flex; align-items: center; justify-content: center;
  gap: 8px; color: #fff; font-size: 18px; font-weight: bold;
  background-color: #2b3a4a;
}
.sidebar-menu { border-right: none; }
.sidebar-menu:not(.el-menu--collapse) { width: 100%; }
</style>
