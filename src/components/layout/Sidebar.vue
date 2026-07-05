<template>
  <aside class="sidebar">
    <div class="sidebar-brand">
      <el-icon :size="24" color="#5e6ad2"><Monitor /></el-icon>
      <span class="brand-text">PC DIY Manager</span>
    </div>

    <el-menu
      :default-active="activeRoute"
      router
      class="sidebar-menu"
      text-color="var(--color-text-on-dark-muted)"
      active-text-color="var(--color-text-on-dark)"
    >
      <el-menu-item index="/dashboard">
        <el-icon><Odometer /></el-icon>
        <span>工作台</span>
      </el-menu-item>
      <el-menu-item index="/config">
        <el-icon><Setting /></el-icon>
        <span>配置方案</span>
      </el-menu-item>
      <el-menu-item index="/inventory">
        <el-icon><Box /></el-icon>
        <span>库存管理</span>
      </el-menu-item>
      <el-menu-item index="/orders">
        <el-icon><List /></el-icon>
        <span>订单管理</span>
      </el-menu-item>
      <el-menu-item index="/knowledge">
        <el-icon><Reading /></el-icon>
        <span>知识库</span>
      </el-menu-item>
    </el-menu>

    <div class="sidebar-footer">
      <div class="sidebar-footer-info">
        <el-icon :size="14"><InfoFilled /></el-icon>
        <span>v1.0.0</span>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const activeRoute = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--color-sidebar);
  display: flex;
  flex-direction: column;
  z-index: 100;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.brand-text {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-on-dark);
  letter-spacing: -0.02em;
}

.sidebar-menu {
  flex: 1;
  background: transparent;
  padding: 12px 8px;
  border: none;
}

.sidebar-menu .el-menu-item {
  margin: 2px 0;
  height: 42px;
  line-height: 42px;
  font-size: 0.9rem;
}

.sidebar-menu .el-menu-item:hover {
  background: var(--color-sidebar-hover);
}

.sidebar-menu .el-menu-item.is-active {
  background: var(--color-sidebar-active);
  color: var(--color-text-on-dark);
}

.sidebar-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  padding: 12px 20px;
}

.sidebar-footer-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-on-dark-muted);
  font-size: 0.75rem;
}
</style>
