<template>
  <el-container class="main-layout">
    <AppSidebar :app-title="appTitle" />

    <el-container>
      <el-header height="60px" class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="avatarUrl" />
              <span class="username">{{ authStore.fullName || authStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon> 个人信息</el-dropdown-item>
                <el-dropdown-item command="settings"><el-icon><Setting /></el-icon> 设置</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { ElMessageBox } from 'element-plus'
import { User, Setting, SwitchButton, ArrowDown } from '@element-plus/icons-vue'
import AppSidebar from '@/views/layout/AppSidebar.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const appTitle = import.meta.env.VITE_APP_TITLE
const currentRouteTitle = computed(() => route.meta.title || '')
const avatarUrl = computed(() => authStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

const handleCommand = async (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    }).then(async () => {
      await authStore.logout()
      const { resetDynamicRoutes } = await import('@/router/dynamicRoutes')
      resetDynamicRoutes(router)
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.main-layout { height: 100vh; }

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-right { display: flex; align-items: center; }

.user-info {
  display: flex; align-items: center; cursor: pointer; padding: 0 10px;
}
.user-info .username { margin: 0 8px; font-size: 14px; color: #333; }

.main-content { background-color: #f0f2f5; padding: 20px; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
