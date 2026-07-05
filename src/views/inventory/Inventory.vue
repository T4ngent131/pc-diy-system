<template>
  <div class="inventory-page">
    <div class="section-header">
      <h2>库存管理</h2>
      <div class="header-actions">
        <el-select v-model="filterType" placeholder="配件类型" clearable size="small" style="width:140px">
          <el-option v-for="(label, key) in componentLabels" :key="key" :label="label.name" :value="key" />
        </el-select>
        <el-input v-model="searchText" placeholder="搜索配件..." clearable size="small" style="width:200px" prefix-icon="Search" />
      </div>
    </div>

    <!-- 库存统计 - Notion tinted 卡片风格 -->
    <div class="stat-row">
      <div class="stat-card" style="background:var(--color-tint-lavender)">
        <span class="stat-value">{{ totalItems }}</span>
        <span class="stat-label">配件种类</span>
      </div>
      <div class="stat-card" style="background:var(--color-tint-mint)">
        <span class="stat-value">{{ totalCount }}</span>
        <span class="stat-label">库存总数</span>
      </div>
      <div class="stat-card" style="background:var(--color-tint-peach)">
        <span class="stat-value">¥{{ totalValue.toLocaleString() }}</span>
        <span class="stat-label">库存总值</span>
      </div>
      <div class="stat-card" style="background:var(--color-tint-rose)">
        <span class="stat-value">{{ lowStockCount }}</span>
        <span class="stat-label">低库存预警</span>
      </div>
    </div>

    <!-- 库存列表 -->
    <el-card>
      <el-table :data="filteredData" stripe highlight-current-row>
        <el-table-column label="配件类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row._typeLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="品牌/型号" min-width="220">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <span class="brand-badge">{{ row.brand }}</span>
              <span>{{ row.model }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="规格" min-width="160">
          <template #default="{ row }">
            <span class="spec-text">{{ getSpecText(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="100" align="right">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="库存" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock <= 3 ? 'danger' : row.stock <= 8 ? 'warning' : 'success'" size="small">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleRestock(row)">入库</el-button>
            <el-button size="small" type="warning" link @click="handleReduce(row)">出库</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 入库/出库弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="配件">
          <el-input :model-value="`${form.item?.brand} ${form.item?.model}`" disabled />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"
import { useInventoryStore } from "@/stores/inventory.js"
import { componentLabels } from "@/api/components.js"
import { ElMessage } from "element-plus"

const inventoryStore = useInventoryStore()
const filterType = ref("")
const searchText = ref("")
const dialogVisible = ref(false)
const dialogAction = ref("")
const form = ref({ item: null, quantity: 1 })

const allItems = computed(() => {
  const items = []
  Object.entries(inventoryStore.components).forEach(([type, list]) => {
    list.forEach((item) => {
      items.push({ ...item, _type: type, _typeLabel: componentLabels[type]?.name || type })
    })
  })
  return items
})

const filteredData = computed(() => {
  let items = allItems.value
  if (filterType.value) items = items.filter((i) => i._type === filterType.value)
  if (searchText.value) {
    const q = searchText.value.toLowerCase()
    items = items.filter((i) => i.model.toLowerCase().includes(q) || i.brand.toLowerCase().includes(q))
  }
  return items
})

const totalItems = computed(() => allItems.value.length)
const totalCount = computed(() => allItems.value.reduce((s, i) => s + i.stock, 0))
const totalValue = computed(() => allItems.value.reduce((s, i) => s + i.price * i.stock, 0))
const lowStockCount = computed(() => inventoryStore.alertCount)

const dialogTitle = computed(() => dialogAction.value === "restock" ? "入库" : "出库")

function getSpecText(item) {
  if (item.cores) return `${item.cores}核${item.threads}线程 ${item.turboClock}GHz`
  if (item.vram) return `${item.vram}GB ${item.vramType}`
  if (item.socket) return `${item.socket} ${item.formFactor}`
  if (item.capacity) return `${item.capacity}GB ${item.speed || ""}`
  if (item.wattage) return `${item.wattage}W ${item.rating}`
  if (item.formFactor && Array.isArray(item.formFactor)) return item.formFactor.join("/")
  if (item.type) return item.type.toUpperCase()
  return ""
}

function handleRestock(item) {
  dialogAction.value = "restock"
  form.value = { item, quantity: 1 }
  dialogVisible.value = true
}

function handleReduce(item) {
  dialogAction.value = "reduce"
  form.value = { item, quantity: 1 }
  dialogVisible.value = true
}

function handleConfirm() {
  const { item, quantity } = form.value
  if (dialogAction.value === "restock") {
    inventoryStore.restock(item._type, item.id, quantity)
    ElMessage.success(`入库 ${quantity} 件 ${item.brand} ${item.model}`)
  } else {
    if (inventoryStore.reduceStock(item._type, item.id, quantity)) {
      ElMessage.success(`出库 ${quantity} 件 ${item.brand} ${item.model}`)
    } else {
      ElMessage.error("库存不足")
    }
  }
  dialogVisible.value = false
}
</script>

<style scoped>
.inventory-page { animation: fadeIn 0.3s ease; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.header-actions {
  display: flex;
  gap: 8px;
}
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}
.stat-card {
  padding: 20px;
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.stat-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--color-text-primary);
}
.stat-label {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.brand-badge {
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  background: var(--color-primary-soft);
  color: var(--color-primary);
  font-weight: 600;
}
.spec-text {
  font-size: 0.85rem;
  color: var(--color-text-secondary);
}
@media (max-width: 768px) {
  .stat-row { grid-template-columns: repeat(2, 1fr); }
}
</style>
