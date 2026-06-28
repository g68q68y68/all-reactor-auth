<template>
  <div class="welcome-page">
    <!-- 导航栏 -->
    <header class="header" :class="{ 'header-scrolled': isScrolled }">
      <div class="container">
        <div class="logo">
          <el-icon><DataAnalysis /></el-icon>
          <span>{{ appTitle }}</span>
        </div>
        <nav class="nav">
          <a href="#features">产品特性</a>
          <a href="#solutions">解决方案</a>
          <a href="#pricing">价格方案</a>
          <a href="#contact">联系我们</a>
          <template v-if="isAuth">
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="28" :src="avatarUrl" />
                <span class="username">{{ authStore.fullName || authStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="admin"><el-icon><Monitor /></el-icon>后台管理</el-dropdown-item>
                  <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <el-button v-else type="primary" size="small" @click="goToLogin">登录</el-button>
        </nav>
        <el-icon class="menu-toggle" @click="mobileMenuVisible = !mobileMenuVisible">
          <Menu />
        </el-icon>
      </div>

      <!-- 移动端菜单 -->
      <transition name="slide-down">
        <div v-if="mobileMenuVisible" class="mobile-menu">
          <a href="#features" @click="mobileMenuVisible = false">产品特性</a>
          <a href="#solutions" @click="mobileMenuVisible = false">解决方案</a>
          <a href="#pricing" @click="mobileMenuVisible = false">价格方案</a>
          <a href="#contact" @click="mobileMenuVisible = false">联系我们</a>
          <template v-if="isAuth">
            <el-button type="primary" size="small" @click="goToAdmin">后台管理</el-button>
          </template>
          <el-button v-else type="primary" size="small" @click="goToLogin">登录</el-button>
        </div>
      </transition>
    </header>

    <!-- 英雄区 -->
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <div class="hero-text">
            <div class="hero-badge">
              <el-icon><Check /></el-icon>
              <span>新一代企业级解决方案</span>
            </div>
            <h1>
              构建安全高效的
              <span class="gradient-text">响应式应用</span>
            </h1>
            <p>
              基于 Spring WebFlux + R2DBC + Vue 3 构建的现代化企业应用框架，
              提供完整的认证授权、数据流式处理和响应式编程体验。
            </p>
            <div class="hero-actions">
              <el-button type="primary" size="large" @click="isAuth ? goToAdmin() : goToLogin()">
                {{ isAuth ? '进入后台' : '开始使用' }}
                <el-icon><ArrowRight /></el-icon>
              </el-button>
              <el-button size="large" @click="scrollToFeatures">
                了解更多
                <el-icon><ArrowDown /></el-icon>
              </el-button>
            </div>
            <div class="hero-stats">
              <div class="stat-item">
                <span class="stat-number" data-count="20000">0</span>
                <span class="stat-label">+ 数据处理能力</span>
              </div>
              <div class="stat-item">
                <span class="stat-number" data-count="99.9">0</span>
                <span class="stat-label">% 可用性</span>
              </div>
              <div class="stat-item">
                <span class="stat-number" data-count="100">0</span>
                <span class="stat-label">% 响应式架构</span>
              </div>
            </div>
          </div>
          <div class="hero-visual">
            <div class="visual-wrapper">
              <!-- 模拟控制台面板 -->
              <div class="dashboard-mock">
                <div class="mock-sidebar">
                  <div class="mock-logo"></div>
                  <div class="mock-nav-item" v-for="i in 5" :key="i"></div>
                </div>
                <div class="mock-main">
                  <div class="mock-header">
                    <div class="mock-breadcrumb"></div>
                    <div class="mock-avatar"></div>
                  </div>
                  <div class="mock-content">
                    <div class="mock-cards">
                      <div class="mock-card" v-for="i in 4" :key="i">
                        <div class="mock-card-icon"></div>
                        <div class="mock-card-text">
                          <div class="mock-line short"></div>
                          <div class="mock-line"></div>
                        </div>
                      </div>
                    </div>
                    <div class="mock-table">
                      <div class="mock-table-header"></div>
                      <div class="mock-table-row" v-for="i in 3" :key="i"></div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="floating-card card-1">
                <el-icon><Check /></el-icon>
                <span>实时数据流</span>
              </div>
              <div class="floating-card card-2">
                <el-icon><Lock /></el-icon>
                <span>安全认证</span>
              </div>
              <div class="floating-card card-3">
                <el-icon><TrendCharts /></el-icon>
                <span>高性能</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 特性区 -->
    <section id="features" class="features">
      <div class="container">
        <div class="section-header">
          <h2>为什么选择我们</h2>
          <p>强大的技术栈组合，为企业应用提供坚实基础</p>
        </div>
        <div class="features-grid">
          <div
              v-for="(feature, index) in features"
              :key="index"
              class="feature-card"
              :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="feature-icon" :style="{ background: feature.color }">
              <el-icon :size="32"><component :is="feature.icon" /></el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 技术栈区 -->
    <section id="solutions" class="tech-stack">
      <div class="container">
        <div class="section-header">
          <h2>核心技术栈</h2>
          <p>现代化技术组合，打造卓越应用</p>
        </div>
        <div class="tech-grid">
          <div v-for="tech in techStack" :key="tech.name" class="tech-item">
            <div class="tech-logo" :style="{ color: tech.color }">
              <el-icon :size="40"><component :is="tech.icon" /></el-icon>
            </div>
            <span class="tech-name">{{ tech.name }}</span>
            <span class="tech-desc">{{ tech.description }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 数据统计区 -->
    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div v-for="stat in stats" :key="stat.label" class="stats-card">
            <div class="stats-number" :data-target="stat.value">
              {{ stat.prefix }}<span>{{ stat.displayValue }}</span>{{ stat.suffix }}
            </div>
            <div class="stats-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA区 -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>准备好开始了吗？</h2>
          <p>立即体验新一代响应式应用框架，开启您的企业级开发之旅</p>
          <div class="cta-actions">
            <el-button type="primary" size="large" @click="isAuth ? goToAdmin() : goToLogin()">
              {{ isAuth ? '进入后台' : '免费试用' }}
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" @click="scrollToContact">
              联系销售
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 联系我们 -->
    <section id="contact" class="contact">
      <div class="container">
        <div class="section-header">
          <h2>联系我们</h2>
          <p>有任何问题？我们随时准备帮助您</p>
        </div>
        <div class="contact-grid">
          <div class="contact-info">
            <div v-for="item in contactInfo" :key="item.label" class="contact-item">
              <div class="contact-icon" :style="{ background: item.color }">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <div>
                <div class="contact-label">{{ item.label }}</div>
                <div class="contact-value">{{ item.value }}</div>
              </div>
            </div>
          </div>
          <div class="contact-form-wrapper">
            <el-form :model="contactForm" class="contact-form">
              <el-form-item>
                <el-input v-model="contactForm.name" placeholder="您的姓名" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="contactForm.email" placeholder="您的邮箱" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="contactForm.subject" placeholder="主题" />
              </el-form-item>
              <el-form-item>
                <el-input
                    v-model="contactForm.message"
                    type="textarea"
                    :rows="4"
                    placeholder="请描述您的需求..."
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" @click="submitContact">
                  发送消息
                  <el-icon><Promotion /></el-icon>
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-brand">
            <div class="logo">
              <el-icon><DataAnalysis /></el-icon>
              <span>{{ appTitle }}</span>
            </div>
            <p>构建现代化企业应用的完整解决方案</p>
            <div class="social-links">
              <el-icon><Message /></el-icon>
              <el-icon><Share /></el-icon>
              <el-icon><Star /></el-icon>
            </div>
          </div>
          <div class="footer-links">
            <div v-for="group in footerLinks" :key="group.title" class="link-group">
              <h4>{{ group.title }}</h4>
              <a v-for="link in group.links" :key="link" href="#">{{ link }}</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <span>&copy; {{ new Date().getFullYear() }} {{ appTitle }}. All rights reserved.</span>
          <div class="footer-bottom-links">
            <a href="#">隐私政策</a>
            <a href="#">服务条款</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'
import {
  DataAnalysis, Check, Lock, TrendCharts, Menu, ArrowRight, ArrowDown,
  Promotion, Message, Share, Star, Monitor, Connection, Document,
  Cellphone, Setting, PieChart, Grid, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const appTitle = import.meta.env.VITE_APP_TITLE || '响应式框架'
const isScrolled = ref(false)
const mobileMenuVisible = ref(false)

const isAuth = computed(() => !!authStore.accessToken)
const avatarUrl = computed(() => authStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

const goToAdmin = () => router.push('/home')
const handleUserCommand = (cmd) => {
  if (cmd === 'admin') router.push('/home')
  else if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning' })
      .then(() => authStore.logout().then(() => router.go(0)))
      .catch(() => {})
  }
}

// 功能特性
const features = [
  {
    icon: 'Connection',
    title: '响应式架构',
    description: '基于 Spring WebFlux 和 R2DBC 构建，支持高并发、非阻塞式数据处理。',
    color: '#4A90D9'
  },
  {
    icon: 'Lock',
    title: '安全认证',
    description: '集成 JWT 和 Spring Security，提供完整的认证授权解决方案。',
    color: '#52C41A'
  },
  {
    icon: 'TrendCharts',
    title: '实时数据流',
    description: '支持 SSE 流式数据传输，实现大数据量的实时展示和监控。',
    color: '#FA8C16'
  },
  {
    icon: 'Document',
    title: '完整文档',
    description: '提供详细的开发文档和示例代码，帮助您快速上手。',
    color: '#722ED1'
  },
  {
    icon: 'Setting',
    title: '灵活配置',
    description: '支持多环境配置和扩展，满足不同业务场景需求。',
    color: '#13C2C2'
  },
  {
    icon: 'Cellphone',
    title: '响应式界面',
    description: '基于 Vue 3 和 Element Plus 构建，适配各种屏幕尺寸。',
    color: '#EB2F96'
  }
]

// 技术栈
const techStack = [
  { name: 'Java 17', icon: 'Monitor', description: '开发语言', color: '#007396' },
  { name: 'Spring WebFlux', icon: 'Connection', description: '响应式框架', color: '#6DB33F' },
  { name: 'R2DBC', icon: 'Grid', description: '响应式数据库', color: '#4479A1' },
  { name: 'Vue 3', icon: 'Monitor', description: '前端框架', color: '#4FC08D' },
  { name: 'Element Plus', icon: 'PieChart', description: 'UI组件库', color: '#409EFF' },
  { name: 'JWT', icon: 'Lock', description: '认证授权', color: '#FF6B6B' }
]

// 统计数据
const stats = [
  { label: '数据处理量', value: '20000+', prefix: '', suffix: ' 条/秒' },
  { label: '响应时间', value: '< 100', prefix: '', suffix: 'ms' },
  { label: '并发支持', value: '10000+', prefix: '', suffix: '' },
  { label: '用户满意度', value: '98', prefix: '', suffix: '%' }
]

// 联系信息
const contactInfo = [
  { icon: 'Message', label: '邮箱', value: 'contact@example.com', color: '#4A90D9' },
  { icon: 'Cellphone', label: '电话', value: '+86 400-888-8888', color: '#52C41A' },
  { icon: 'Monitor', label: '地址', value: '北京市朝阳区xxx路xxx号', color: '#FA8C16' }
]

// 页脚链接
const footerLinks = [
  {
    title: '产品',
    links: ['产品特性', '解决方案', '价格方案', '更新日志']
  },
  {
    title: '支持',
    links: ['文档中心', 'API 参考', '常见问题', '技术支持']
  },
  {
    title: '公司',
    links: ['关于我们', '招聘信息', '博客', '联系我们']
  }
]

// 联系表单
const contactForm = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
})

// 滚动监听
const handleScroll = () => {
  isScrolled.value = window.scrollY > 60
}

// 数字动画
const animateNumbers = () => {
  const statNumbers = document.querySelectorAll('.stat-number')
  statNumbers.forEach(el => {
    const target = parseInt(el.dataset.count)
    if (isNaN(target)) return

    let current = 0
    const increment = target / 60
    const timer = setInterval(() => {
      current += increment
      if (current >= target) {
        current = target
        clearInterval(timer)
      }
      el.textContent = Math.floor(current)
    }, 20)
  })
}

// 滚动到指定区域
const scrollToFeatures = () => {
  document.querySelector('#features')?.scrollIntoView({ behavior: 'smooth' })
}

const scrollToContact = () => {
  document.querySelector('#contact')?.scrollIntoView({ behavior: 'smooth' })
}

// 跳转到登录
const goToLogin = () => {
  router.push('/login')
}

// 提交联系表单
const submitContact = () => {
  if (!contactForm.value.name || !contactForm.value.email) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success('感谢您的留言，我们会尽快与您联系！')
  contactForm.value = { name: '', email: '', subject: '', message: '' }
}

// 生命周期
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  // 延迟执行动画
  setTimeout(animateNumbers, 500)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ========== 基础样式 ========== */
.welcome-page {
  min-height: 100vh;
  background: #ffffff;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-header h2 {
  font-size: 36px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.section-header p {
  font-size: 18px;
  color: #666;
}

/* ========== 导航栏 ========== */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  border-bottom: 1px solid transparent;
}

.header-scrolled {
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  border-bottom-color: #f0f0f0;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
}

.logo .el-icon {
  font-size: 28px;
  color: #4A90D9;
}

.nav {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav a {
  color: #333;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
  position: relative;
}

.nav a::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: #4A90D9;
  transition: width 0.3s;
}

.nav a:hover {
  color: #4A90D9;
}

.nav a:hover::after {
  width: 100%;
}

.nav .user-info {
  display: flex; align-items: center; gap: 6px; cursor: pointer;
  font-size: 14px; color: #333;
}

.menu-toggle {
  display: none;
  font-size: 24px;
  cursor: pointer;
  color: #333;
}

.mobile-menu {
  display: none;
  flex-direction: column;
  padding: 20px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  gap: 16px;
}

.mobile-menu a {
  color: #333;
  text-decoration: none;
  padding: 8px 0;
  font-size: 15px;
}

.mobile-menu a:hover {
  color: #4A90D9;
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* ========== 英雄区 ========== */
.hero {
  padding: 140px 0 80px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8edf5 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(74, 144, 217, 0.1);
  border-radius: 20px;
  color: #4A90D9;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 24px;
}

.hero-badge .el-icon {
  font-size: 16px;
}

.hero-text h1 {
  font-size: 48px;
  font-weight: 800;
  line-height: 1.2;
  color: #1a1a2e;
  margin-bottom: 20px;
}

.gradient-text {
  background: linear-gradient(135deg, #4A90D9, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-text p {
  font-size: 18px;
  line-height: 1.8;
  color: #555;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
}

.hero-actions .el-button .el-icon {
  margin-left: 4px;
}

.hero-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
}

.stat-label {
  font-size: 14px;
  color: #888;
  margin-top: 4px;
}

.hero-visual {
  position: relative;
}

.visual-wrapper {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

/* 控制台模拟图 */
.dashboard-mock {
  width: 100%; height: 360px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 12px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,.2);
}

.mock-sidebar {
  width: 70px; background: rgba(255,255,255,.05);
  padding: 16px 10px; display: flex; flex-direction: column; gap: 14px; align-items: center;
}

.mock-logo {
  width: 36px; height: 36px; border-radius: 8px;
  background: linear-gradient(135deg, #4A90D9, #764ba2);
  margin-bottom: 8px;
}

.mock-nav-item {
  width: 36px; height: 8px; border-radius: 4px;
  background: rgba(255,255,255,.15);
}

.mock-nav-item:nth-child(2) { background: rgba(255,255,255,.35); width: 44px; }
.mock-nav-item:nth-child(3) { width: 30px; }
.mock-nav-item:nth-child(5) { width: 40px; }

.mock-main { flex: 1; padding: 16px 20px; display: flex; flex-direction: column; gap: 16px; }

.mock-header {
  display: flex; justify-content: space-between; align-items: center;
}

.mock-breadcrumb {
  width: 120px; height: 10px; border-radius: 5px;
  background: rgba(255,255,255,.12);
}

.mock-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, #52C41A, #13C2C2);
}

.mock-cards { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }

.mock-card {
  background: rgba(255,255,255,.06); border-radius: 8px;
  padding: 14px; display: flex; align-items: center; gap: 12px;
}

.mock-card-icon {
  width: 32px; height: 32px; border-radius: 6px; flex-shrink: 0;
}

.mock-card:nth-child(1) .mock-card-icon { background: rgba(74,144,217,.4); }
.mock-card:nth-child(2) .mock-card-icon { background: rgba(82,196,26,.4); }
.mock-card:nth-child(3) .mock-card-icon { background: rgba(250,140,22,.4); }
.mock-card:nth-child(4) .mock-card-icon { background: rgba(235,47,150,.4); }

.mock-card-text { display: flex; flex-direction: column; gap: 6px; flex: 1; }

.mock-line {
  height: 8px; border-radius: 4px; background: rgba(255,255,255,.12);
}

.mock-line.short { width: 50%; }

.mock-table {
  flex: 1; display: flex; flex-direction: column; gap: 8px;
}

.mock-table-header {
  height: 10px; border-radius: 5px; background: rgba(255,255,255,.15); width: 100%;
}

.mock-table-row {
  height: 8px; border-radius: 4px; background: rgba(255,255,255,.06);
}

.mock-table-row:nth-child(2) { width: 90%; }
.mock-table-row:nth-child(3) { width: 85%; }
.mock-table-row:nth-child(4) { width: 92%; }

.floating-card {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  animation: float 3s ease-in-out infinite;
}

.floating-card .el-icon {
  font-size: 18px;
}

.card-1 {
  top: 20px;
  left: -20px;
  animation-delay: 0s;
}

.card-2 {
  bottom: 40px;
  left: -10px;
  animation-delay: 1s;
}

.card-3 {
  top: 50%;
  right: -20px;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* ========== 特性区 ========== */
.features {
  padding: 80px 0;
  background: #ffffff;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.feature-card {
  padding: 32px 24px;
  background: #f8f9fc;
  border-radius: 12px;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease both;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}

.feature-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 20px;
  color: #1a1a2e;
  margin-bottom: 10px;
}

.feature-card p {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
}

/* ========== 技术栈区 ========== */
.tech-stack {
  padding: 80px 0;
  background: #f8f9fc;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 30px;
}

.tech-item {
  text-align: center;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.tech-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.tech-logo {
  margin-bottom: 12px;
}

.tech-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.tech-desc {
  font-size: 13px;
  color: #999;
}

/* ========== 统计数据区 ========== */
.stats-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  color: #fff;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.stats-card {
  text-align: center;
}

.stats-number {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #4A90D9, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats-label {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

/* ========== CTA区 ========== */
.cta-section {
  padding: 80px 0;
  background: #ffffff;
}

.cta-content {
  text-align: center;
  padding: 60px;
  background: linear-gradient(135deg, #f5f7fa, #e8edf5);
  border-radius: 16px;
}

.cta-content h2 {
  font-size: 36px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.cta-content p {
  font-size: 18px;
  color: #555;
  margin-bottom: 32px;
}

.cta-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* ========== 联系区 ========== */
.contact {
  padding: 80px 0;
  background: #f8f9fc;
}

.contact-grid {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 50px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.contact-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.contact-label {
  font-size: 13px;
  color: #999;
}

.contact-value {
  font-size: 15px;
  color: #1a1a2e;
  font-weight: 500;
}

.contact-form-wrapper {
  background: #fff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.contact-form .el-form-item {
  margin-bottom: 16px;
}

.contact-form .el-button {
  width: 100%;
}

/* ========== 页脚 ========== */
.footer {
  background: #1a1a2e;
  color: #fff;
  padding: 60px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 60px;
  margin-bottom: 40px;
}

.footer-brand .logo {
  color: #fff;
  margin-bottom: 12px;
}

.footer-brand p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  margin-bottom: 16px;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-links .el-icon {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: color 0.3s;
}

.social-links .el-icon:hover {
  color: #fff;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.link-group h4 {
  font-size: 16px;
  margin-bottom: 12px;
  color: #fff;
}

.link-group a {
  display: block;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-size: 14px;
  padding: 4px 0;
  transition: color 0.3s;
}

.link-group a:hover {
  color: #4A90D9;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 20px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.footer-bottom-links {
  display: flex;
  gap: 20px;
}

.footer-bottom-links a {
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  transition: color 0.3s;
}

.footer-bottom-links a:hover {
  color: #fff;
}

/* ========== 动画 ========== */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .hero-visual {
    order: -1;
  }

  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .tech-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .nav {
    display: none;
  }

  .menu-toggle {
    display: block;
  }

  .mobile-menu {
    display: flex;
  }

  .hero {
    padding: 100px 0 60px;
  }

  .hero-text h1 {
    font-size: 32px;
  }

  .hero-stats {
    gap: 20px;
    flex-wrap: wrap;
  }

  .stat-number {
    font-size: 24px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .tech-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .contact-grid {
    grid-template-columns: 1fr;
  }

  .footer-links {
    grid-template-columns: 1fr 1fr;
  }

  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .floating-card {
    display: none;
  }

  .cta-content {
    padding: 30px 20px;
  }

  .hero-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .tech-grid {
    grid-template-columns: 1fr;
  }

  .footer-links {
    grid-template-columns: 1fr;
  }
}
</style>