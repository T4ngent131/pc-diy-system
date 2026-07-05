<template>
  <div class="user-track">
    <div class="page-header">
      <h1>订单查询</h1>
      <p>查看您的装机订单当前状态</p>
    </div>

    <!-- 订单列表 -->
    <el-card>
      <el-table :data="orderStore.orders" stripe highlight-current-row>
        <el-table-column prop="id" label="订单号" width="190">
          <template #default="{ row }">
            <span style="font-family:var(--font-mono);font-weight:500;font-size:0.85rem">{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="配置概览" min-width="280">
          <template #default="{ row }">
            <div class="config-preview">
              <span v-for="(val, key) in row.config" :key="key" class="config-chip">
                {{ val }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="120" align="right">
          <template #default="{ row }">¥{{ row.totalPrice.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="orderStore.statusColorMap[row.status] || 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="160">
          <template #default="{ row }">{{ row.createdAt }}</template>
        </el-table-column>
      </el-table>

      <div v-if="orderStore.orders.length === 0" style="text-align:center;padding:40px;color:var(--color-text-muted)">
        <el-icon :size="48" color="var(--color-text-muted)"><Box /></el-icon>
        <p style="margin-top:12px">暂无订单</p>
        <router-link to="/build">
          <el-button type="primary" style="margin-top:16px">开始装机</el-button>
        </router-link>
      </div>
    </el-card>

    <!-- 订单状态说明卡片 - Notion 风格 -->
    <div class="status-guide">
      <h3>订单状态说明</h3>
      <div class="status-steps">
        <div class="status-step" v-for="s in statusSteps" :key="s.label">
          <div class="step-icon" :style="{ background: s.color + '18' }">
            <el-icon :size="20" :color="s.color"><component :is="s.icon" /></el-icon>
          </div>
          <div class="step-info">
            <span class="step-label">{{ s.label }}</span>
            <span class="step-desc">{{ s.desc }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useOrderStore } from "@/stores/orders.js"

const orderStore = useOrderStore()

const statusSteps = [
  { icon: "Edit", label: "待确认", desc: "订单已提交，等待客服确认配件可用性", color: "var(--color-warning)" },
  { icon: "Box", label: "待备货", desc: "订单已确认，正在采购配件", color: "var(--color-info)" },
  { icon: "Tools", label: "组装中", desc: "配件全部到齐，工程师正在组装调试", color: "var(--color-primary)" },
  { icon: "CircleCheck", label: "已完成", desc: "设备已组装完毕，准备发货", color: "var(--color-success)" },
]
</script>

<style scoped>
.user-track { padding: 32px 24px; max-width: 1000px; margin: 0 auto; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { color: var(--color-text-muted); margin-top: 4px; }

.config-preview { display: flex; flex-wrap: wrap; gap: 4px; }
.config-chip {
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: 4px;
  background: var(--color-surface);
  color: var(--color-text-secondary);
}

.status-guide {
  margin-top: 24px;
  padding: 24px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
}
.status-guide h3 { font-size: 1rem; font-weight: 600; margin-bottom: 16px; }
.status-steps { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.status-step { display: flex; align-items: center; gap: 10px; padding: 12px; border-radius: var(--radius-md); }
.step-icon {
  width: 40px; height: 40px;
  display: flex; align-items: center; justify-content: center;
  border-radius: var(--radius-md); flex-shrink: 0;
}
.step-info { display: flex; flex-direction: column; gap: 2px; }
.step-label { font-weight: 600; font-size: 0.85rem; }
.step-desc { font-size: 0.75rem; color: var(--color-text-muted); line-height: 1.3; }

@media (max-width: 640px) {
  .status-steps { grid-template-columns: repeat(2, 1fr); }
}
</style>
