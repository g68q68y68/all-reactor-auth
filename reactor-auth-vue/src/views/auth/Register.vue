<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>{{ appTitle }}</h1>
        <p>创建新账户</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-button"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <el-link type="primary" @click="router.push('/login')">已有账号？立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register as registerApi } from '@/api/auth'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { validators } from '@/utils/validator'

const router = useRouter()

const appTitle = import.meta.env.VITE_APP_TITLE
const loading = ref(false)
const registerFormRef = ref()

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [{ validator: validators.username, trigger: 'blur' }],
  password: [{ validator: validators.password, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  email: [{ validator: validators.email, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await registerApi({
        username: registerForm.username,
        password: registerForm.password,
        email: registerForm.email
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error) {
      ElMessage.error(error.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.register-header p {
  color: #999;
  margin-top: 8px;
  font-size: 14px;
}

.register-form .el-form-item {
  margin-bottom: 22px;
}

.register-button {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
}
</style>
