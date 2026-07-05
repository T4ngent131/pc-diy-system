<template>
  <div class="config-table">
    <el-table :data="componentTypes" stripe>
      <el-table-column label="配件类型" width="140">
        <template #default="{ row }">
          <div class="type-cell">
            <el-icon><component :is="row.icon" /></el-icon>
            <span>{{ row.label }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="已选配件" min-width="260">
        <template #default="{ row }">
          <div v-if="config[row.key]" class="selected-cell">
            <span class="selected-name">{{ config[row.key].brand }} {{ config[row.key].model }}</span>
            <span v-if="row.key === 'cpu'" class="selected-detail">{{ config[row.key].cores }}核{{ config[row.key].threads }}线程</span>
            <span v-else-if="row.key === 'gpu'" class="selected-detail">{{ config[row.key].vram }}GB {{ config[row.key].vramType }}</span>
            <span v-else-if="row.key === 'ram'" class="selected-detail">{{ config[row.key].capacity }}GB {{ config[row.key].speed }}MHz</span>
            <span v-else-if="row.key === 'storage'" class="selected-detail">{{ config[row.key].capacity }}GB {{ config[row.key].type }}</span>
            <span v-else-if="row.key === 'psu'" class="selected-detail">{{ config[row.key].wattage }}W {{ config[row.key].rating }}</span>
            <span v-else-if="row.key === 'case'" class="selected-detail">{{ config[row.key].formFactor.join("/") }}</span>
            <span v-else-if="row.key === 'cooler'" class="selected-detail">{{ config[row.key].type.toUpperCase() }}</span>
            <span v-else class="selected-detail">{{ config[row.key].price }}元</span>
          </div>
          <span v-else class="empty-cell">未选择</span>
        </template>
      </el-table-column>
      <el-table-column label="价格" width="100" align="right">
        <template #default="{ row }">
          <span v-if="config[row.key]" class="price-cell">¥{{ config[row.key].price }}</span>
          <span v-else class="empty-cell">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="$emit('select', row.key)">
            {{ config[row.key] ? "更换" : "选择" }}
          </el-button>
          <el-button v-if="config[row.key]" type="danger" link size="small" @click="$emit('remove', row.key)">
            移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="config-summary">
      <div class="summary-item">
        <span class="summary-label">合计</span>
        <span class="summary-price">¥{{ totalPrice.toLocaleString() }}</span>
      </div>
      <div class="summary-item" v-if="totalPower > 0">
        <span class="summary-label">估算功耗</span>
        <span class="summary-value">{{ totalPower }}W</span>
      </div>
      <div class="summary-item" v-if="recommendedPSU > 0">
        <span class="summary-label">推荐电源</span>
        <span class="summary-value">{{ recommendedPSU }}W</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { componentLabels } from "@/api/components.js"

const props = defineProps({
  config: { type: Object, required: true },
  totalPrice: { type: Number, default: 0 },
  totalPower: { type: Number, default: 0 },
  recommendedPSU: { type: Number, default: 0 },
})

defineEmits(["select", "remove"])

const componentTypes = computed(() => {
  return Object.entries(componentLabels).map(([key, val]) => ({
    key,
    label: val.name,
    icon: val.icon,
  }))
})
</script>

<style scoped>
.config-table {
  margin-bottom: 16px;
}
.type-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}
.selected-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.selected-name {
  font-weight: 500;
  color: var(--color-text-primary);
}
.selected-detail {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.empty-cell {
  color: var(--color-text-muted);
}
.price-cell {
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  color: var(--color-text-primary);
}
.config-summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  padding: 12px 16px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-top: none;
  border-radius: 0 0 var(--radius-lg) var(--radius-lg);
}
.summary-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.summary-label {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
.summary-price {
  font-size: 1rem;
  font-weight: 700;
  color: var(--color-primary);
  font-variant-numeric: tabular-nums;
}
.summary-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-text-primary);
}
</style>
