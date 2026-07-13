<template>
  <div class="user-layout">
    <!-- Top Nav - Stripe 风格 -->
    <nav class="top-nav" :class="{ scrolled: isScrolled }">
      <div class="nav-inner">
        <router-link to="/" class="nav-brand">
          <div class="brand-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none"><rect width="28" height="28" rx="6" fill="#5e6ad2"/><path d="M8 14L12 18L20 10" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <span class="brand-name">PC<span class="brand-accent">DIY</span></span>
        </router-link>

        <div class="nav-links">
          <router-link to="/build" class="nav-link" active-class="active">自定义装机</router-link>
          <router-link to="/plans" class="nav-link" active-class="active">推荐配置</router-link>
          <router-link to="/guides" class="nav-link" active-class="active">选购指南</router-link>
        </div>

        <div class="nav-actions">
          <template v-if="authStore.isLoggedIn">
            <span class="user-pill">
              <el-icon><User /></el-icon>
              {{ authStore.user?.username }}
            </span>
            <el-button link type="primary" @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <router-link to="/auth?mode=login" class="login-link">登录</router-link>
          </template>
          <router-link to="/order" class="cta-button">开始装机</router-link>
        </div>

        <!-- Mobile menu toggle -->
        <button class="mobile-toggle" @click="mobileOpen = !mobileOpen">
          <el-icon :size="22"><component :is="mobileOpen ? 'Close' : 'Menu'" /></el-icon>
        </button>
      </div>

      <!-- Mobile menu -->
      <div class="mobile-menu" v-show="mobileOpen">
        <router-link to="/build" class="mobile-link" @click="mobileOpen = false">自定义装机</router-link>
        <router-link to="/plans" class="mobile-link" @click="mobileOpen = false">推荐配置</router-link>
        <router-link to="/guides" class="mobile-link" @click="mobileOpen = false">选购指南</router-link>
        <router-link v-if="!authStore.isLoggedIn" to="/auth?mode=login" class="mobile-link" @click="mobileOpen = false">登录 / 注册</router-link>
        <button v-else class="mobile-link mobile-button" @click="handleLogout">退出登录</button>
        <router-link to="/order" class="mobile-cta" @click="mobileOpen = false">开始装机</router-link>
      </div>
    </nav>

    <!-- Page Content -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer - Notion 风格 -->
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-grid">
          <div class="footer-col brand-col">
            <div class="footer-brand">
              <svg width="24" height="24" viewBox="0 0 28 28" fill="none"><rect width="28" height="28" rx="6" fill="#5e6ad2"/><path d="M8 14L12 18L20 10" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span>PC<span style="color:var(--color-primary)">DIY</span></span>
            </div>
            <p class="footer-desc">让每个人都能轻松组建属于自己的理想电脑</p>
          </div>
          <div class="footer-col">
            <h4>服务</h4>
            <router-link to="/build">自定义装机</router-link>
            <router-link to="/plans">推荐配置</router-link>
            <router-link to="/order">提交订单</router-link>
          </div>
          <div class="footer-col">
            <h4>学习</h4>
            <router-link to="/guides">选购指南</router-link>
            <router-link to="/guides/1">CPU 选购</router-link>
            <router-link to="/guides/3">显卡选购</router-link>
          </div>
          <div class="footer-col">
            <h4>关于</h4>
          </div>
        </div>
        <div class="footer-bottom">
          <span>&copy; 2026 PCDIY. All rights reserved.</span>
          <div class="footer-badge">v2.0 · 用户版</div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import { useAuthStore } from "@/stores/auth.js"

const router = useRouter()
const authStore = useAuthStore()
const isScrolled = ref(false)
const mobileOpen = ref(false)

function handleScroll() {
  isScrolled.value = window.scrollY > 20
}

function handleLogout() {
  authStore.logout()
  mobileOpen.value = false
  ElMessage.success("已退出登录")
  router.push("/")
}

onMounted(() => window.addEventListener("scroll", handleScroll, { passive: true }))
onUnmounted(() => window.removeEventListener("scroll", handleScroll))
</script>

<style scoped>
.user-layout { min-height: 100vh; display: flex; flex-direction: column; }

/* Top Nav */
.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  transition: all 0.25s ease;
  background: transparent;
}
.top-nav.scrolled {
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Brand */
.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}
.brand-icon { display: flex; align-items: center; }
.brand-name { font-size: 1.15rem; font-weight: 700; color: var(--color-text-primary); letter-spacing: -0.03em; }
.brand-accent { color: var(--color-primary); }

/* Nav Links */
.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-link {
  padding: 8px 14px;
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  text-decoration: none;
  transition: all 0.15s;
}
.nav-link:hover { color: var(--color-primary); background: var(--color-primary-soft); }
.nav-link.active { color: var(--color-primary); background: var(--color-primary-soft); }

/* Actions */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.cta-button {
  display: inline-flex;
  align-items: center;
  padding: 8px 20px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}
.cta-button:hover { background: var(--color-primary-hover); transform: translateY(-1px); }
.login-link {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none;
}
.login-link:hover { color: var(--color-primary); }
.user-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  max-width: 140px;
  font-size: 0.85rem;
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mobile-toggle { display: none; background: none; border: none; cursor: pointer; color: var(--color-text-primary); padding: 4px; }

/* Mobile Menu */
.mobile-menu {
  display: none;
  padding: 8px 24px 16px;
  background: var(--color-canvas);
  border-bottom: 1px solid var(--color-border);
}
.mobile-link {
  display: block;
  width: 100%;
  padding: 10px 0;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  text-decoration: none;
  border-bottom: 1px solid var(--color-border-soft);
}
.mobile-button {
  border: none;
  background: transparent;
  text-align: left;
  cursor: pointer;
}
.mobile-cta {
  display: block;
  text-align: center;
  margin-top: 12px;
  padding: 10px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-weight: 500;
  text-decoration: none;
}

.main-content { flex: 1; padding-top: 64px; }

/* Footer */
.footer {
  background: var(--color-canvas);
  border-top: 1px solid var(--color-border);
  padding: 48px 0 24px;
}
.footer-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 32px;
  margin-bottom: 32px;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 8px;
}
.footer-desc { font-size: 0.85rem; color: var(--color-text-muted); line-height: 1.6; max-width: 260px; }
.footer-col h4 {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}
.footer-col a {
  display: block;
  font-size: 0.85rem;
  color: var(--color-text-muted);
  text-decoration: none;
  padding: 4px 0;
  transition: color 0.15s;
}
.footer-col a:hover { color: var(--color-primary); }

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid var(--color-border-soft);
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.footer-badge {
  padding: 4px 10px;
  border-radius: var(--radius-full);
  background: var(--color-primary-soft);
  color: var(--color-primary);
  font-weight: 500;
}

@media (max-width: 768px) {
  .nav-links, .nav-actions { display: none; }
  .mobile-toggle { display: flex; }
  .mobile-menu { display: block; }
  .footer-grid { grid-template-columns: 1fr 1fr; }
}
</style>
