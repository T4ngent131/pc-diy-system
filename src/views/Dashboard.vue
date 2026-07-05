<template>
  <div class="dashboard">
    <!-- 统计卡片 - Stripe 卡片风格 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label" :style="{ borderTop: `3px solid ${stat.color}` }">
        <div class="stat-icon" :style="{ background: stat.bg }">
          <el-icon :size="22" :color="stat.color"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-trend" v-if="stat.trend">
          <el-icon :size="12"><Top /></el-icon>
          <span>{{ stat.trend }}</span>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <!-- 常用功能 -->
      <el-card class="dash-card">
        <template #header>
          <div class="card-header">
            <span><el-icon><Lightning /></el-icon> 快捷操作</span>
          </div>
        </template>
        <div class="quick-actions">
          <div class="action-item" v-for="action in quickActions" :key="action.label" @click="action.action">
            <el-icon :size="28" :color="action.color"><component :is="action.icon" /></el-icon>
            <span>{{ action.label }}</span>
          </div>
        </div>
      </el-card>

      <!-- 最近配置 -->
      <el-card class="dash-card">
        <template #header>
          <div class="card-header">
            <span><el-icon><Setting /></el-icon> 最近配置</span>
            <el-button text type="primary" size="small" @click="$router.push('/config')">查看全部</el-button>
          </div>
        </template>
        <div v-if="configStore.savedConfigs.length === 0" class="empty-state">
          <el-icon :size="40" color="var(--color-text-muted)"><FolderOpened /></el-icon>
          <p>暂无保存的配置方案</p>
        </div>
        <div v-else class="recent-configs">
          <div v-for="cfg in configStore.savedConfigs.slice(0, 3)" :key="cfg.id" class="config-item">
            <div class="config-item-main">
              <el-icon color="var(--color-primary)"><Document /></el-icon>
              <div>
                <div class="config-name">{{ cfg.name }}</div>
                <div class="config-meta">¥{{ cfg.totalPrice.toLocaleString() }} · {{ cfg.createdAt.slice(0, 10) }}</div>
              </div>
            </div>
            <el-button text type="primary" size="small" @click="configStore.loadConfig(cfg)">加载</el-button>
          </div>
        </div>
      </el-card>

      <!-- 待处理订单 -->
      <el-card class="dash-card">
        <template #header>
          <div class="card-header">
            <span><el-icon><List /></el-icon> 待处理订单</span>
            <el-button text type="primary" size="small" @click="$router.push('/orders')">查看全部</el-button>
          </div>
        </template>
        <div v-if="orderStore.pendingCount === 0" class="empty-state">
          <el-icon :size="40" color="var(--color-text-muted)"><CircleCheck /></el-icon>
          <p>暂无待处理订单</p>
        </div>
        <div v-else class="order-list">
          <div v-for="order in orderStore.orders.slice(0, 3)" :key="order.id" class="order-item">
            <div class="order-item-main">
              <div class="order-id">{{ order.id }}</div>
              <div class="order-customer">{{ order.customer }} · ¥{{ order.totalPrice.toLocaleString() }}</div>
            </div>
            <el-tag :type="orderStore.statusColorMap[order.status] || 'info'" size="small">{{ order.status }}</el-tag>
          </div>
        </div>
      </el-card>

      <!-- 库存预警 -->
      <el-card class="dash-card">
        <template #header>
          <div class="card-header">
            <span><el-icon><Box /></el-icon> 库存预警</span>
            <el-button text type="primary" size="small" @click="$router.push('/inventory')">查看全部</el-button>
          </div>
        </template>
        <div v-if="inventoryStore.alertCount === 0" class="empty-state">
          <el-icon :size="40" color="var(--color-success)"><CircleCheck /></el-icon>
          <p>库存充足</p>
        </div>
        <div v-else class="alert-list">
          <div v-for="item in inventoryStore.lowStockItems.slice(0, 3)" :key="item.id" class="alert-item">
            <div class="alert-item-main">
              <div class="alert-name">{{ item.brand }} {{ item.model }}</div>
              <div class="alert-stock">库存: <span style="color:var(--color-danger);font-weight:600">{{ item.stock }}</span></div>
            </div>
            <el-tag size="small" type="danger">补货</el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, h } from "vue"
import { useRouter } from "vue-router"
import { useConfigStore } from "@/stores/config.js"
import { useInventoryStore } from "@/stores/inventory.js"
import { useOrderStore } from "@/stores/orders.js"

const router = useRouter()
const configStore = useConfigStore()
const inventoryStore = useInventoryStore()
const orderStore = useOrderStore()

const stats = computed(() => [
  { label: "已保存方案", value: configStore.savedConfigs.length, icon: "Setting", color: "#5e6ad2", bg: "#eef0ff", trend: null },
  { label: "配件总数", value: inventoryStore.totalItems, icon: "Coin", color: "#10b981", bg: "#d1fae5", trend: null },
  { label: "库存预警", value: inventoryStore.alertCount, icon: "WarningFilled", color: "#f59e0b", bg: "#fef3c7", trend: null },
  { label: "待处理订单", value: orderStore.pendingCount, icon: "List", color: "#3b82f6", bg: "#dbeafe", trend: null },
])

const quickActions = [
  { label: "新建配置", icon: "Plus", color: "#5e6ad2", action: () => { configStore.clearConfig(); router.push("/config") } },
  { label: "配件库", icon: "Coin", color: "#10b981", action: () => router.push("/inventory") },
  { label: "新订单", icon: "DocumentAdd", color: "#f59e0b", action: () => router.push("/orders") },
  { label: "知识库", icon: "Reading", color: "#3b82f6", action: () => router.push("/knowledge") },
]
</script>

<style scoped>
.dashboard { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: var(--color-canvas);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s ease;
  cursor: default;
  position: relative;
}
.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
}
.stat-label {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.dash-card { height: fit-content; }
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-header span { display: flex; align-items: center; gap: 6px; font-weight: 600; }
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s;
}
.action-item:hover {
  background: var(--color-surface);
}
.action-item span {
  font-size: 0.8rem;
  color: var(--color-text-secondary);
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px;
  color: var(--color-text-muted);
  gap: 8px;
}
.empty-state p { font-size: 0.9rem; }
.recent-configs, .order-list, .alert-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.config-item, .order-item, .alert-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  transition: background 0.15s;
}
.config-item:hover, .order-item:hover, .alert-item:hover {
  background: var(--color-surface);
}
.config-item-main, .order-item-main, .alert-item-main {
  display: flex;
  align-items: center;
  gap: 10px;
}
.config-name { font-weight: 500; font-size: 0.9rem; }
.config-meta { font-size: 0.78rem; color: var(--color-text-muted); }
.order-id { font-weight: 500; font-size: 0.85rem; }
.order-customer { font-size: 0.8rem; color: var(--color-text-muted); }
.alert-name { font-weight: 500; font-size: 0.85rem; }
.alert-stock { font-size: 0.8rem; color: var(--color-text-muted); }

@media (max-width: 1024px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .dashboard-grid { grid-template-columns: 1fr; }
}
</style>
