<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="680px" @close="handleClose">
    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="filterBrand" placeholder="品牌" clearable size="small" style="width:120px">
        <el-option v-for="b in brands" :key="b" :label="b" :value="b" />
      </el-select>
      <el-input v-model="searchText" placeholder="搜索型号..." clearable size="small" style="width:200px" prefix-icon="Search" />
    </div>

    <!-- 列表 -->
    <el-table :data="filteredList" stripe highlight-current-row @row-click="handleSelect">
      <el-table-column label="型号" min-width="200">
        <template #default="{ row }">
          <div class="item-name">
            <span class="brand-tag">{{ row.brand }}</span>
            <span>{{ row.model }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        v-for="col in dynamicColumns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        align="center"
      >
        <template #default="{ row }">
          <template v-if="col.prop === 'price'">
            <span class="price-tag">¥{{ row.price }}</span>
          </template>
          <template v-else-if="col.prop === 'stock'">
            <el-tag :type="row.stock <= 3 ? 'danger' : row.stock <= 8 ? 'warning' : 'success'" size="small">
              {{ row.stock }}
            </el-tag>
          </template>
          <template v-else>
            {{ row[col.prop] }}
          </template>
        </template>
      </el-table-column>
      <el-table-column label="库存" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.stock <= 3 ? 'danger' : 'success'" size="small">{{ row.stock }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue"
import { componentsData } from "@/api/components.js"

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  type: { type: String, default: null },
})

const emit = defineEmits(["update:modelValue", "select"])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => { visible.value = v })

const searchText = ref("")
const filterBrand = ref("")

const dynamicColumns = computed(() => {
  const t = props.type
  if (!t) return []
  const cols = [{ prop: "price", label: "价格", width: 100 }]
  if (t === "cpu") {
    cols.unshift({ prop: "cores", label: "核心", width: 70 }, { prop: "threads", label: "线程", width: 70 })
  } else if (t === "gpu") {
    cols.unshift({ prop: "vram", label: "显存", width: 80 })
  } else if (t === "motherboard") {
    cols.unshift({ prop: "socket", label: "插槽", width: 100 }, { prop: "formFactor", label: "版型", width: 90 })
  } else if (t === "ram") {
    cols.unshift({ prop: "capacity", label: "容量", width: 70 }, { prop: "speed", label: "频率", width: 80 })
  } else if (t === "storage") {
    cols.unshift({ prop: "capacity", label: "容量", width: 80 }, { prop: "formFactor", label: "规格", width: 100 })
  } else if (t === "psu") {
    cols.unshift({ prop: "wattage", label: "功率", width: 80 }, { prop: "rating", label: "认证", width: 80 })
  } else if (t === "case") {
    cols.unshift({ prop: "formFactor", label: "兼容版型", width: 120 })
  } else if (t === "cooler") {
    cols.unshift({ prop: "type", label: "类型", width: 70 })
  }
  return cols
})

const brands = computed(() => {
  if (!props.type) return []
  const items = componentsData[props.type] || []
  return [...new Set(items.map((i) => i.brand))]
})

const filteredList = computed(() => {
  if (!props.type) return []
  let items = componentsData[props.type] || []
  if (filterBrand.value) items = items.filter((i) => i.brand === filterBrand.value)
  if (searchText.value) {
    const q = searchText.value.toLowerCase()
    items = items.filter((i) => i.model.toLowerCase().includes(q) || i.brand.toLowerCase().includes(q))
  }
  return items
})

const dialogTitle = computed(() => {
  const labels = { cpu: "CPU 处理器", gpu: "显卡", motherboard: "主板", ram: "内存", storage: "存储", psu: "电源", case: "机箱", cooler: "散热器" }
  return `选择${labels[props.type] || "配件"}`
})

function handleSelect(row) {
  emit("select", props.type, row)
  handleClose()
}

function handleClose() {
  visible.value = false
  emit("update:modelValue", false)
  searchText.value = ""
  filterBrand.value = ""
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}
.item-name {
  display: flex;
  align-items: center;
  gap: 8px;
}
.brand-tag {
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  background: var(--color-primary-soft);
  color: var(--color-primary);
  font-weight: 600;
}
.price-tag {
  font-weight: 600;
  color: var(--color-primary);
  font-variant-numeric: tabular-nums;
}
</style>
