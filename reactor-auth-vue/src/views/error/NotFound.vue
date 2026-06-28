<template>
  <div class="not-found-page">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <p class="error-desc">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.not-found-content {
  text-align: center;
}

.error-code {
  font-size: 96px;
  font-weight: bold;
  color: #dcdfe6;
  margin: 0;
  line-height: 1;
}

.error-desc {
  font-size: 18px;
  color: #909399;
  margin: 20px 0 30px;
}
</style>
