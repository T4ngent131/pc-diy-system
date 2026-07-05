<template>
  <div class="orders-page">
    <div class="section-header">
      <h2>订单管理</h2>
      <el-button type="primary" size="small" @click="showNewOrder = true">
        <el-icon><Plus /></el-icon> 新建订单
      </el-button>
    </div>

    <!-- 订单列表 -->
    <el-card>
      <el-table :data="orderStore.orders" stripe highlight-current-row>
        <el-table-column prop="id" label="订单号" width="180">
          <template #default="{ row }">
            <span style="font-weight:500;font-size:0.85rem;font-family:var(--font-mono)">{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="customer" label="客户" width="100" />
        <el-table-column label="配置方案" min-width="200">
          <template #default="{ row }">
            <el-popover placement="bottom" :width="300" trigger="click">
              <template #reference>
                <el-button text size="small" @click.prevent>查看方案</el-button>
              </template>
              <div class="config-popover">
                <div v-for="(val, key) in row.config" :key="key" class="popover-row">
                  <span class="popover-label">{{ componentLabels[key]?.name || key }}</span>
                  <span class="popover-value">{{ val }}</span>
                </div>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="120" align="right">
          <template #default="{ row }">¥{{ row.totalPrice.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-dropdown @command="(s) => orderStore.updateStatus(row.id, s)">
              <el-tag :type="orderStore.statusColorMap[row.status] || 'info'" size="small" style="cursor:pointer">
                {{ row.status }} <el-icon><ArrowDown /></el-icon>
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="s in orderStore.statusOptions" :key="s" :command="s">{{ s }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ row.createdAt }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-popconfirm title="确认删除？" @confirm="orderStore.deleteOrder(row.id)">
              <template #reference>
                <el-button text type="danger" size="small"><el-icon><Delete /></el-icon></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建订单弹窗 -->
    <el-dialog v-model="showNewOrder" title="新建订单" width="500px">
      <el-form :model="newOrder" label-width="90px">
        <el-form-item label="客户姓名">
          <el-input v-model="newOrder.customer" placeholder="输入客户姓名" />
        </el-form-item>
        <el-form-item label="配置方案">
          <el-select v-model="newOrder.configIndex" placeholder="选择已保存方案" style="width:100%">
            <el-option
              v-for="(cfg, i) in configStore.savedConfigs"
              :key="cfg.id"
              :label="`${cfg.name} (¥${cfg.totalPrice.toLocaleString()})`"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="总价">
          <el-input :model-value="`¥${estimatedPrice.toLocaleString()}`" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewOrder = false">取消</el-button>
        <el-button type="primary" :disabled="!canSubmit" @click="handleCreateOrder">创建订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from "vue"
import { useOrderStore } from "@/stores/orders.js"
import { useConfigStore } from "@/stores/config.js"
import { useInventoryStore } from "@/stores/inventory.js"
import { componentLabels } from "@/api/components.js"
import { ElMessage } from "element-plus"

const orderStore = useOrderStore()
const configStore = useConfigStore()
const inventoryStore = useInventoryStore()
const showNewOrder = ref(false)

const newOrder = reactive({
  customer: "",
  configIndex: null,
})

const estimatedPrice = computed(() => {
  if (newOrder.configIndex === null || newOrder.configIndex === undefined) return 0
  return configStore.savedConfigs[newOrder.configIndex]?.totalPrice || 0
})

const canSubmit = computed(() => newOrder.customer && newOrder.configIndex !== null)

function handleCreateOrder() {
  const cfg = configStore.savedConfigs[newOrder.configIndex]
  const configNames = {}
  Object.entries(cfg.config).forEach(([key, val]) => {
    if (val) configNames[key] = `${val.brand} ${val.model}`
  })

  orderStore.addOrder({
    customer: newOrder.customer,
    config: configNames,
    totalPrice: cfg.totalPrice,
  })

  // 扣减库存
  Object.entries(cfg.config).forEach(([type, item]) => {
    if (item) inventoryStore.reduceStock(type, item.id, 1)
  })

  ElMessage.success("订单创建成功")
  showNewOrder.value = false
  newOrder.customer = ""
  newOrder.configIndex = null
}
</script>

<style scoped>
.orders-page { animation: fadeIn 0.3s ease; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.config-popover {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.popover-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 0.85rem;
}
.popover-label {
  color: var(--color-text-muted);
}
.popover-value {
  font-weight: 500;
  text-align: right;
}
</style>
