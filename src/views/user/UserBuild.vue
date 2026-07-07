<template>
  <div class="user-build">
    <div class="page-header">
      <h1>自定义装机</h1>
      <p>按需选择配件，系统实时校验兼容性</p>
    </div>

    <div class="build-layout">
      <!-- 左侧：配件选择面板 -->
      <div class="build-main">
        <!-- 进度指示器 -->
        <div class="progress-steps">
          <div
            v-for="(ct, i) in componentTypes"
            :key="ct.key"
            class="progress-dot"
            :class="{ filled: currentConfig[ct.key], active: activeCategory === ct.key }"
            @click="activeCategory = ct.key"
            :title="ct.label"
          >
            <el-icon :size="ct.key === activeCategory ? 22 : 16">
              <component :is="ct.icon" />
            </el-icon>
          </div>
        </div>

        <!-- 当前分类配件列表 - Notion tinted cards -->
        <transition name="fade-slide" mode="out-in">
          <div class="category-panel" :key="activeCategory">
            <div class="category-header">
              <h3>{{ componentLabels[activeCategory]?.name }}</h3>
              <span class="category-count">{{ filteredItems.length }} 款可选</span>
            </div>
            <div class="filter-row">
              <el-select v-model="filterBrand" placeholder="品牌" clearable size="small" style="width:120px">
                <el-option v-for="b in brands" :key="b" :label="b" :value="b" />
              </el-select>
              <el-input v-model="searchText" placeholder="搜索..." clearable size="small" style="width:160px" prefix-icon="Search" />
            </div>
            <div class="items-grid" v-if="filteredItems.length > 0">
              <div
                v-for="item in filteredItems"
                :key="item.id"
                class="item-card"
                :class="{ selected: currentConfig[activeCategory]?.id === item.id }"
                @click="selectItem(item)"
              >
                <div class="item-header">
                  <span class="item-brand">{{ item.brand }}</span>
                  <el-tag size="small" :type="item.stock <= 3 ? 'danger' : 'success'" effect="plain">
                    {{ item.stock <= 3 ? "即将售罄" : "有货" }}
                  </el-tag>
                </div>
                <div class="item-model">{{ item.model }}</div>
                <div class="item-specs">
                  <span v-for="spec in getSpecs(item)" :key="spec" class="item-spec">{{ spec }}</span>
                </div>
                <div class="item-footer">
                  <span class="item-price">¥{{ item.price }}</span>
                  <span class="item-select-btn">{{ currentConfig[activeCategory]?.id === item.id ? "已选" : "选择" }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-items">
              <el-icon :size="40" color="var(--color-text-muted)"><Search /></el-icon>
              <p>未找到匹配的配件</p>
            </div>
          </div>
        </transition>
      </div>

      <!-- 右侧：配置总览 -->
      <div class="build-sidebar">
        <!-- 兼容性状态 -->
        <div class="compat-card" :class="{ ok: compat.compatible, warn: !compat.compatible }">
          <el-icon :size="22">
            <CircleCheck v-if="compat.compatible" />
            <WarningFilled v-else />
          </el-icon>
          <div>
            <div class="compat-title">{{ compat.compatible ? "兼容性良好" : "存在兼容问题" }}</div>
            <div v-for="issue in compat.issues" :key="issue" class="compat-issue">{{ issue }}</div>
            <div v-for="warn in compat.warnings" :key="warn" class="compat-warn">{{ warn }}</div>
          </div>
        </div>

        <!-- 配置清单 -->
        <div class="build-summary">
          <h4>当前配置</h4>
          <div class="summary-items">
            <div
              v-for="ct in componentTypes"
              :key="ct.key"
              class="summary-row"
              :class="{ empty: !currentConfig[ct.key] }"
              @click="activeCategory = ct.key"
            >
              <el-icon :size="14"><component :is="ct.icon" /></el-icon>
              <span class="summary-label">{{ ct.label }}</span>
              <span class="summary-value" v-if="currentConfig[ct.key]">
                {{ currentConfig[ct.key].brand }} {{ currentConfig[ct.key].model }}
              </span>
              <span class="summary-empty" v-else>点击选择</span>
              <span class="summary-price" v-if="currentConfig[ct.key]">¥{{ currentConfig[ct.key].price }}</span>
            </div>
          </div>

          <div class="summary-total">
            <div class="total-row">
              <span>合计</span>
              <span class="total-price">¥{{ totalPrice.toLocaleString() }}</span>
            </div>
            <div class="total-row" v-if="totalPower > 0">
              <span>估算功耗</span>
              <span>{{ totalPower }}W</span>
            </div>
          </div>

          <button
            class="submit-btn"
            :disabled="filledCount < 4"
            @click="handleSubmit"
          >
            {{ filledCount < 4 ? `请至少选择 4 个配件（已选 ${filledCount} 个）` : "提交配置下单" }}
          </button>
        </div>
      </div>
    </div>

    <!-- 下单弹窗 -->
    <el-dialog v-model="orderDialog" title="提交配置单" width="500px">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="orderForm.name" placeholder="请输入您的姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="orderForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.note" type="textarea" :rows="3" placeholder="其他需求" />
        </el-form-item>
        <el-form-item label="配置总价">
          <span style="font-size:1.1rem;font-weight:700;color:var(--color-primary)">¥{{ totalPrice.toLocaleString() }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder" :disabled="!orderForm.name || !orderForm.phone">
          确认提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import { componentsData, componentLabels } from "@/api/components.js"
import { useConfigStore } from "@/stores/config.js"
import { useInventoryStore } from "@/stores/inventory.js"
import { useOrderStore } from "@/stores/orders.js"
import { checkCompatibility, estimateTotalPower } from "@/utils/compatibility.js"
import { ElMessage } from "element-plus"

const router = useRouter()
const configStore = useConfigStore()
const inventoryStore = useInventoryStore()
const orderStore = useOrderStore()

const activeCategory = ref("cpu")
const filterBrand = ref("")
const searchText = ref("")
const orderDialog = ref(false)

const currentConfig = reactive({
  cpu: null, gpu: null, motherboard: null, ram: null,
  storage: null, psu: null, case: null, cooler: null,
})

const orderForm = reactive({ name: "", phone: "", note: "" })

function restoreSelectedPlan() {
  const rawPlan = sessionStorage.getItem("selectedPlan")
  if (!rawPlan) return

  try {
    const savedPlan = JSON.parse(rawPlan)
    Object.entries(savedPlan.config || {}).forEach(([type, item]) => {
      if (item) currentConfig[type] = item
    })
    sessionStorage.removeItem("selectedPlan")
  } catch (error) {
    console.warn("恢复推荐配置失败:", error)
    sessionStorage.removeItem("selectedPlan")
  }
}

const componentTypes = computed(() =>
  Object.entries(componentLabels).map(([key, val]) => ({ key, label: val.name, icon: val.icon }))
)

const brands = computed(() => {
  const items = componentsData[activeCategory.value] || []
  return [...new Set(items.map((i) => i.brand))]
})

const filteredItems = computed(() => {
  let items = componentsData[activeCategory.value] || []
  if (filterBrand.value) items = items.filter((i) => i.brand === filterBrand.value)
  if (searchText.value) {
    const q = searchText.value.toLowerCase()
    items = items.filter((i) => i.model.toLowerCase().includes(q))
  }
  return items
})

const totalPrice = computed(() => Object.values(currentConfig).reduce((s, i) => s + (i?.price || 0), 0))
const totalPower = computed(() => estimateTotalPower(currentConfig))
const filledCount = computed(() => Object.values(currentConfig).filter(Boolean).length)
const compat = computed(() => checkCompatibility(currentConfig))

function getSpecs(item) {
  const s = []
  if (item.cores) s.push(`${item.cores}核/${item.threads}线程`)
  if (item.turboClock) s.push(`${item.turboClock}GHz`)
  if (item.vram) s.push(`${item.vram}GB`)
  if (item.socket) s.push(item.socket)
  if (item.formFactor && !Array.isArray(item.formFactor)) s.push(item.formFactor)
  if (item.capacity) s.push(`${item.capacity}GB`)
  if (item.speed) s.push(`${item.speed}MHz`)
  if (item.wattage) s.push(`${item.wattage}W`)
  if (item.rating) s.push(item.rating)
  if (item.type) s.push(item.type)
  if (Array.isArray(item.formFactor)) s.push(item.formFactor.join("/"))
  return s
}

function selectItem(item) {
  currentConfig[activeCategory.value] = item
}

onMounted(() => {
  restoreSelectedPlan()
})

function handleSubmit() {
  if (filledCount.value < 4) {
    ElMessage.warning(`请至少选择 4 个配件（当前已选 ${filledCount.value} 个）`)
    return
  }
  if (!compat.value.compatible) {
    ElMessage.warning("存在兼容性问题，请先解决")
    return
  }
  orderDialog.value = true
}

function confirmOrder() {
  const configNames = {}
  Object.entries(currentConfig).forEach(([key, item]) => {
    if (item) configNames[key] = `${item.brand} ${item.model}`
  })

  // 通过管理后台的 orders 和 inventory stores 同步
  const order = {
    customer: orderForm.name,
    config: configNames,
    totalPrice: totalPrice.value,
    phone: orderForm.phone,
    note: orderForm.note,
  }
  orderStore.addOrder(order)

  Object.entries(currentConfig).forEach(([type, item]) => {
    if (item) inventoryStore.reduceStock(type, item.id, 1)
  })

  ElMessage.success("订单提交成功！我们将会尽快与您联系。")
  orderDialog.value = false
  orderForm.name = ""
  orderForm.phone = ""
  orderForm.note = ""
  router.push("/track")
}
</script>

<style scoped>
.user-build { padding: 32px 24px; max-width: 1200px; margin: 0 auto; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; letter-spacing: -0.02em; }
.page-header p { color: var(--color-text-muted); margin-top: 4px; font-size: 0.9rem; }

.build-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
  align-items: start;
}

/* Progress */
.progress-steps {
  display: flex;
  gap: 6px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.progress-dot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background: var(--color-surface);
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}
.progress-dot.active { background: var(--color-primary); color: #fff; border-color: var(--color-primary); }
.progress-dot.filled { background: var(--color-primary-soft); color: var(--color-primary); }
.progress-dot.filled.active { background: var(--color-primary); color: #fff; }
.progress-dot:hover { transform: scale(1.1); }

/* Category panel */
.category-panel { min-height: 300px; }
.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.category-header h3 { font-size: 1.05rem; font-weight: 600; }
.category-count { font-size: 0.8rem; color: var(--color-text-muted); }
.filter-row { display: flex; gap: 8px; margin-bottom: 16px; }

.items-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
.item-card {
  padding: 14px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.item-card:hover { border-color: var(--color-primary); box-shadow: var(--shadow-sm); }
.item-card.selected {
  border-color: var(--color-primary);
  background: var(--color-primary-soft);
  box-shadow: 0 0 0 2px var(--color-primary);
}
.item-header { display: flex; justify-content: space-between; align-items: center; }
.item-brand { font-size: 0.72rem; padding: 2px 6px; border-radius: 4px; background: rgba(94,106,210,0.1); color: var(--color-primary); font-weight: 600; }
.item-model { font-size: 0.9rem; font-weight: 600; }
.item-specs { display: flex; flex-wrap: wrap; gap: 4px; }
.item-spec { font-size: 0.72rem; padding: 1px 6px; border-radius: 4px; background: var(--color-surface); color: var(--color-text-muted); }
.item-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 4px; }
.item-price { font-weight: 700; color: var(--color-primary); font-size: 0.9rem; }
.item-select-btn { font-size: 0.78rem; color: var(--color-primary); font-weight: 500; }
.item-card.selected .item-select-btn { color: var(--color-text-muted); }

.empty-items { text-align: center; padding: 60px 0; color: var(--color-text-muted); }

/* Sidebar */
.build-sidebar { display: flex; flex-direction: column; gap: 12px; position: sticky; top: 80px; }

.compat-card {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px;
  border-radius: var(--radius-lg);
  font-size: 0.85rem;
  line-height: 1.5;
}
.compat-card.ok { background: var(--color-success-soft); color: var(--color-success); }
.compat-card.warn { background: var(--color-danger-soft); color: var(--color-danger); }
.compat-title { font-weight: 600; }
.compat-issue { font-size: 0.78rem; margin-top: 4px; }
.compat-warn { font-size: 0.78rem; margin-top: 4px; color: var(--color-warning); }

.build-summary {
  background: var(--color-canvas);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 20px;
}
.build-summary h4 { font-size: 0.9rem; font-weight: 600; margin-bottom: 12px; }
.summary-items { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.summary-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 0.82rem;
  transition: background 0.15s;
}
.summary-row:hover { background: var(--color-surface); }
.summary-row.empty { color: var(--color-text-muted); }
.summary-label { flex-shrink: 0; width: 40px; font-weight: 500; color: var(--color-text-secondary); }
.summary-value { flex: 1; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.summary-empty { flex: 1; font-style: italic; }
.summary-price { font-weight: 600; color: var(--color-text-primary); font-variant-numeric: tabular-nums; }

.summary-total {
  border-top: 1px solid var(--color-border);
  padding-top: 12px;
  margin-bottom: 16px;
}
.total-row { display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 4px; }
.total-price { font-size: 1.15rem; font-weight: 700; color: var(--color-primary); }

.submit-btn {
  width: 100%;
  padding: 12px;
  border-radius: var(--radius-md);
  border: none;
  background: var(--color-primary);
  color: #fff;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.submit-btn:hover:not(:disabled) { background: var(--color-primary-hover); transform: translateY(-1px); }
.submit-btn:disabled { background: var(--color-border); color: var(--color-text-muted); cursor: not-allowed; }

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.2s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-10px); }

@media (max-width: 900px) {
  .build-layout { grid-template-columns: 1fr; }
  .build-sidebar { position: static; }
}
</style>
