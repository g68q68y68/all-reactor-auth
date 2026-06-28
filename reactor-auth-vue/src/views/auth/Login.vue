<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>{{ appTitle }}</h1>
        <p>登录到您的账户</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <el-link type="primary" @click="router.push('/register')">还没有账号？立即注册</el-link>
        </div>
      </el-form>

      <div class="demo-accounts">
        <el-divider>测试账号</el-divider>
        <div class="account-item">
          <span class="label">管理员:</span><span class="value">admin / admin123</span>
          <el-button size="small" @click="fillAccount('admin','admin123')">填充</el-button>
        </div>
        <div class="account-item">
          <span class="label">普通用户:</span><span class="value">user / user123</span>
          <el-button size="small" @click="fillAccount('user','user123')">填充</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { User, Lock } from '@element-plus/icons-vue'
import { validators } from '@/utils/validator'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const appTitle = import.meta.env.VITE_APP_TITLE
const loading = ref(false)
const loginFormRef = ref()

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ validator: validators.username, trigger: 'blur' }],
  password: [{ validator: validators.password, trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const result = await authStore.login(loginForm)
      if (result.success) {
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      }
    } finally {
      loading.value = false
    }
  })
}

const fillAccount = (u, p) => { loginForm.username = u; loginForm.password = p }
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.login-header p {
  color: #999;
  margin-top: 8px;
  font-size: 14px;
}

.login-form .el-form-item {
  margin-bottom: 22px;
}

.login-button {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}

.demo-accounts { margin-top: 8px; }
.account-item {
  display: flex; align-items: center; gap: 8px;
  font-size: 13px; padding: 6px 12px;
  background: #f5f7fa; border-radius: 4px; margin-bottom: 6px;
}
.account-item .label { color: #666; min-width: 60px; }
.account-item .value { color: #333; font-family: monospace; flex: 1; }
</style>
