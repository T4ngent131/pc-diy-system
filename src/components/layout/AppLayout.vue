<template>
  <div class="layout">
    <Sidebar />
    <div class="layout-main">
      <Header />
      <div class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from "./Sidebar.vue"
import Header from "./Header.vue"
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-surface);
}
.layout-main {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.layout-content {
  flex: 1;
  padding: 24px;
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;
}
</style>
