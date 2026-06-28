<template>
  <!-- 有可见子节点 → 可折叠分组 -->
  <el-sub-menu v-if="visibleChildren.length" :index="firstChildPath">
    <template #title>
      <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
      <span @click.stop="$router.push(firstChildPath)">{{ item.title }}</span>
    </template>
    <SidebarMenuItem
      v-for="child in visibleChildren"
      :key="child.id"
      :item="child"
      :parent-path="fullPath"
    />
  </el-sub-menu>

  <!-- 无子节点的叶子 -->
  <el-menu-item v-else :index="fullPath">
    <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
    <span>{{ item.title }}</span>
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: { type: Object, required: true },
  parentPath: { type: String, default: '' }
})

// 跳过 hidden 的子节点，其可见子节点上提并继承路径
const visibleChildren = computed(() => {
  const result = []
  for (const child of props.item.children || []) {
    if (child.status !== 1 || child.type !== 'MENU') continue
    if (child.hidden) {
      // hidden 节点路径累加到子节点上
      const hp = child.path || ''
      const hiddenFullPath = hp.startsWith('/') ? hp : `${fullPath.value}/${hp}`.replace(/\/+/g, '/')
      for (const grandchild of child.children || []) {
        if (grandchild.status === 1 && grandchild.type === 'MENU' && !grandchild.hidden) {
          // 将 hidden 节点的路径注入子节点
          result.push({ ...grandchild, _inheritedPath: hiddenFullPath })
        }
      }
    } else {
      result.push(child)
    }
  }
  return result
})

const p = props.item.path || ''
// _inheritedPath 是 hidden 父节点累加的路径
const inheritedPath = props.item._inheritedPath || props.parentPath
const fullPath = computed(() =>
  p.startsWith('/') ? p : `${inheritedPath}/${p}`.replace(/\/+/g, '/')
)

const firstChildPath = computed(() => {
  const first = visibleChildren.value[0]
  if (!first) return fullPath.value
  const fp = first.path || ''
  return fp.startsWith('/') ? fp : `${fullPath.value}/${fp}`.replace(/\/+/g, '/')
})
</script>
