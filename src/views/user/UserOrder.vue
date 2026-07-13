<template>
  <div class="user-order">
    <div class="page-header">
      <h1>提交订单</h1>
      <p>填入联系信息，我们的工程师将尽快联系您确认配置</p>
    </div>

    <div class="order-layout">
      <div class="order-form">
        <el-card>
          <template #header><span style="font-weight:600">联系信息</span></template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="姓名">
              <el-input v-model="form.name" placeholder="请输入您的姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="微信">
              <el-input v-model="form.wechat" placeholder="选填，方便沟通" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.note" type="textarea" :rows="3" placeholder="其他需求或特殊要求" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 16px;">
          <template #header><span style="font-weight:600">选择配置方案</span></template>
          <el-radio-group v-model="configSource" style="margin-bottom:16px">
            <el-radio value="plan">使用推荐方案</el-radio>
            <el-radio value="list">从已保存方案选择</el-radio>
          </el-radio-group>

          <div v-if="configSource === 'plan'">
            <el-select v-model="selectedPlan" placeholder="选择一个推荐方案" style="width:100%">
              <el-option
                v-for="plan in plans"
                :key="plan.id"
                :label="`${plan.name} (¥${plan.price.toLocaleString()})`"
                :value="plan.id"
              />
            </el-select>
            <div v-if="selectedPlanDetail" class="plan-preview">
              <div v-for="spec in selectedPlanDetail.specs" :key="spec.label" class="preview-row">
                <span class="preview-label">{{ spec.label }}</span>
                <span class="preview-value">{{ spec.value }}</span>
              </div>
            </div>
          </div>
          <div v-else>
            <el-select v-model="selectedConfig" placeholder="选择已保存方案" style="width:100%">
              <el-option
                v-for="(cfg, i) in configStore.savedConfigs"
                :key="cfg.id"
                :label="`${cfg.name} (¥${cfg.totalPrice.toLocaleString()})`"
                :value="i"
              />
            </el-select>
          </div>
        </el-card>
      </div>

      <div class="order-sidebar">
        <el-card>
          <template #header><span style="font-weight:600">订单摘要</span></template>
          <div class="summary-section">
            <div class="summary-row1">
              <span>配置方案</span>
              <span>{{ orderTitle || "未选择" }}</span>
            </div>
            <div class="summary-row1 total">
              <span>预估总价</span>
              <span class="order-price">¥{{ orderPrice.toLocaleString() }}</span>
            </div>
          </div>
          <el-button
            type="primary"
            style="width:100%;margin-top:12px"
            :disabled="!canSubmit"
            :loading="submitting"
            @click="handleSubmit"
          >
            提交订单
          </el-button>
          <p style="font-size:0.75rem;color:var(--color-text-muted);margin-top:8px;text-align:center">
            提交后，工程师将在 24 小时内联系您
          </p>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import { recommendedBuilds } from "@/api/user.js"
import { useConfigStore } from "@/stores/config.js"
import { useOrderStore } from "@/stores/orders.js"
import { useInventoryStore } from "@/stores/inventory.js"
import { useAuthStore } from "@/stores/auth.js"
import { ElMessage } from "element-plus"

const router = useRouter()
const configStore = useConfigStore()
const orderStore = useOrderStore()
const inventoryStore = useInventoryStore()
const authStore = useAuthStore()

const form = reactive({ name: "", phone: "", wechat: "", note: "" })
const configSource = ref("plan")
const selectedPlan = ref(null)
const selectedConfig = ref(null)
const submitting = ref(false)

const plans = recommendedBuilds

const selectedPlanDetail = computed(() => plans.find((p) => p.id === selectedPlan.value))

const orderTitle = computed(() => {
  if (configSource.value === "plan" && selectedPlanDetail.value) return selectedPlanDetail.value.name
  if (configSource.value === "list" && selectedConfig.value !== null) {
    return configStore.savedConfigs[selectedConfig.value]?.name
  }
  return ""
})

const orderPrice = computed(() => {
  if (configSource.value === "plan" && selectedPlanDetail.value) return selectedPlanDetail.value.price
  if (configSource.value === "list" && selectedConfig.value !== null) {
    return configStore.savedConfigs[selectedConfig.value]?.totalPrice || 0
  }
  return 0
})

const canSubmit = computed(() => form.name && form.phone && (selectedPlan.value || selectedConfig.value !== null))

onMounted(async () => {
  if (authStore.user) {
    form.name = authStore.user.username || form.name
    form.phone = authStore.user.phone || form.phone
  }
  await orderStore.fetchOrders()
  await inventoryStore.fetchComponents()
})

async function handleSubmit() {
  let configData, totalPrice, configNames

  if (configSource.value === "plan" && selectedPlanDetail.value) {
    const plan = selectedPlanDetail.value
    configNames = {}
    plan.specs.forEach((s) => {
      configNames[s.label] = s.value
    })
    totalPrice = plan.price
    configData = plan.config
  } else if (configSource.value === "list" && selectedConfig.value !== null) {
    const cfg = configStore.savedConfigs[selectedConfig.value]
    configNames = {}
    Object.entries(cfg.config).forEach(([key, item]) => {
      if (item) configNames[key] = `${item.brand} ${item.model}`
    })
    totalPrice = cfg.totalPrice
    configData = cfg.config
  }

  submitting.value = true
  try {
    await orderStore.addOrder({
      customer: form.name,
      config: configNames,
      totalPrice,
      phone: form.phone,
      wechat: form.wechat,
      note: form.note,
      items: buildOrderItems(configData),
    })

    if (!orderStore.backendReady && configData) {
      for (const [type, item] of Object.entries(configData)) {
        const componentId = typeof item === "string" ? item : item?.id
        if (componentId) await inventoryStore.reduceStock(type, componentId, 1, "订单扣减")
      }
    }

    ElMessage.success("订单提交成功！我们将尽快联系您确认配置。")
    router.push("/track")
  } catch (error) {
    ElMessage.error(error.message || "订单提交失败")
  } finally {
    submitting.value = false
  }
}

function buildOrderItems(configData) {
  if (!configData) return []

  return Object.entries(configData)
    .filter(([, item]) => item?.id || typeof item === "string")
    .map(([type, item]) => ({
      type,
      componentId: typeof item === "string" ? item : item.id,
    }))
}
</script>

<style scoped>
.user-order { padding: 32px 24px; max-width: 1000px; margin: 0 auto; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { color: var(--color-text-muted); margin-top: 4px; }

.order-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  align-items: start;
}

.plan-preview {
  margin-top: 12px;
  padding: 12px;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.preview-row { display: flex; justify-content: space-between; font-size: 0.82rem; }
.preview-label { color: var(--color-text-muted); }
.preview-value { font-weight: 500; }

.summary-section { display: flex; flex-direction: column; gap: 8px; }
.summary-row1 { display: flex; justify-content: space-between; font-size: 0.85rem; }
.summary-row1.total { border-top: 1px solid var(--color-border); padding-top: 8px; margin-top: 4px; }
.order-price { font-size: 1.1rem; font-weight: 700; color: var(--color-primary); }

@media (max-width: 768px) {
  .order-layout { grid-template-columns: 1fr; }
}
</style>
