<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #e6f7ff;">
              <el-icon :size="32" color="#1890ff"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.userCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #f6ffed;">
              <el-icon :size="32" color="#52c41a"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">数据条目</div>
              <div class="stat-value">{{ stats.dataCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #fff7e6;">
              <el-icon :size="32" color="#fa8c16"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待处理</div>
              <div class="stat-value">{{ stats.pendingCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #fff1f0;">
              <el-icon :size="32" color="#f5222d"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">异常</div>
              <div class="stat-value">{{ stats.errorCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>系统概览</span>
          </template>
          <div class="welcome-section">
            <h2>欢迎回来，{{ username }}</h2>
            <p>这是您的安全管理仪表盘</p>
            <div class="quick-links">
              <el-button type="primary" @click="$router.push('/users')">用户管理</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>系统信息</span>
          </template>
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">系统版本</span>
              <span class="info-value">{{ appVersion }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">当前角色</span>
              <span class="info-value">
                <el-tag v-for="role in roles" :key="role" size="small" style="margin-left: 4px;">{{ role }}</el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">登录时间</span>
              <span class="info-value">{{ loginTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { useAuthStore } from '@/store/modules/auth'
import dayjs from 'dayjs'
import { User, DataLine, Warning, CircleClose } from '@element-plus/icons-vue'

const authStore = useAuthStore()

const appVersion = import.meta.env.VITE_APP_VERSION
const username = computed(() => authStore.fullName || authStore.username)
const roles = computed(() => authStore.roles || [])
const loginTime = computed(() => dayjs().format('YYYY-MM-DD HH:mm:ss'))

const stats = reactive({
  userCount: 128,
  dataCount: 1024,
  pendingCount: 5,
  errorCount: 0
})
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

.stat-card {
  border-radius: 8px;
}

.stat-body {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.welcome-section {
  padding: 20px 0;
}

.welcome-section h2 {
  font-size: 22px;
  color: #303133;
  margin-bottom: 8px;
}

.welcome-section p {
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}

.quick-links {
  display: flex;
  gap: 12px;
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.info-label {
  color: #909399;
}

.info-value {
  color: #303133;
}
</style>
