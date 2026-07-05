<template>
  <header class="header">
    <div class="header-left">
      <h1 class="page-title">{{ pageTitle }}</h1>
    </div>
    <div class="header-right">
      <el-badge :value="inventoryStore.alertCount" :hidden="inventoryStore.alertCount === 0" class="header-badge">
        <el-button text @click="showAlertDialog = true">
          <el-icon :size="18"><Bell /></el-icon>
        </el-button>
      </el-badge>
      <el-dropdown trigger="click">
        <span class="user-info">
          <el-icon :size="18"><User /></el-icon>
          <span>管理员</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 低库存提醒 -->
    <el-dialog v-model="showAlertDialog" title="库存预警" width="500px" :close-on-click-modal="false">
      <div v-if="inventoryStore.lowStockItems.length === 0" style="text-align:center;padding:20px;color:var(--color-text-muted)">
        <el-icon :size="48" color="var(--color-success)"><CircleCheck /></el-icon>
        <p style="margin-top:12px">暂无低库存商品</p>
      </div>
      <el-table v-else :data="inventoryStore.lowStockItems" stripe style="width:100%">
        <el-table-column prop="type" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ componentLabels[row.type]?.name || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="型号" min-width="180">
          <template #default="{ row }">{{ row.brand }} {{ row.model }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock <= 1 ? 'danger' : 'warning'" size="small">{{ row.stock }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </header>
</template>

<script setup>
import { ref, computed } from "vue"
import { useRoute } from "vue-router"
import { useInventoryStore } from "@/stores/inventory.js"
import { componentLabels } from "@/api/components.js"
import { ElMessage } from "element-plus"

const route = useRoute()
const inventoryStore = useInventoryStore()
const showAlertDialog = ref(false)

const pageTitle = computed(() => route.meta?.title || "PC DIY 管理系统")

function handleLogout() {
  ElMessage.info("退出登录（演示版本）")
}
</script>

<style scoped>
.header {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: var(--color-canvas);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 50;
}
.header-left {
  display: flex;
  align-items: center;
}
.page-title {
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-badge {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  transition: background 0.15s;
}
.user-info:hover {
  background: var(--color-surface);
}
</style>
