<template>
  <div class="user-plans">
    <div class="page-header">
      <h1>推荐配置</h1>
      <p>经过专业搭配的成熟方案，直接下单或微调皆可</p>
    </div>

    <div class="plans-list" v-for="plan in plans" :key="plan.id" :id="plan.id">
      <div class="plan-detail-card" :style="{ background: plan.tint }">
        <div class="plan-detail-header">
          <div>
            <div class="plan-detail-name">{{ plan.name }}</div>
            <div class="plan-detail-summary">{{ plan.summary }}</div>
          </div>
          <div class="plan-detail-price">
            <span class="price-label">参考总价</span>
            <span class="price-value">¥{{ plan.price.toLocaleString() }}</span>
            <router-link to="/build" class="plan-use-btn" @click="loadPlan(plan)">使用此方案</router-link>
          </div>
        </div>

        <div class="plan-specs-detail">
          <div class="spec-row" v-for="spec in plan.specs" :key="spec.label">
            <span class="spec-label">{{ spec.label }}</span>
            <span class="spec-value">{{ spec.value }}</span>
          </div>
        </div>

        <div class="plan-performance">
          <div class="perf-item">
            <span class="perf-label">游戏性能</span>
            <span class="perf-stars">{{ plan.performance.gaming }}</span>
          </div>
          <div class="perf-item">
            <span class="perf-label">生产力</span>
            <span class="perf-stars">{{ plan.performance.productivity }}</span>
          </div>
          <div class="perf-item">
            <span class="perf-label">性价比</span>
            <span class="perf-stars">{{ plan.performance.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { recommendedBuilds } from "@/api/user.js"
import { useConfigStore } from "@/stores/config.js"
import { componentsData } from "@/api/components.js"

const plans = recommendedBuilds
const configStore = useConfigStore()

function loadPlan(plan) {
  configStore.clearConfig()
  Object.entries(plan.config).forEach(([type, id]) => {
    if (id && componentsData[type]) {
      const item = componentsData[type].find((c) => c.id === id)
      if (item) {
        // We need to set this on the build page
        // Store the plan data for the build page to use
        sessionStorage.setItem("planConfig", JSON.stringify({ type, id: item.id }))
      }
    }
  })
  // Store complete plan in sessionStorage
  sessionStorage.setItem("selectedPlan", JSON.stringify({
    ...plan,
    config: Object.fromEntries(
      Object.entries(plan.config).map(([type, id]) => {
        const items = componentsData[type]
        const item = items?.find((c) => c.id === id)
        return [type, item || null]
      })
    )
  }))
}
</script>

<style scoped>
.user-plans { padding: 32px 24px; max-width: 1000px; margin: 0 auto; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.page-header { margin-bottom: 32px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; letter-spacing: -0.02em; }
.page-header p { color: var(--color-text-muted); margin-top: 4px; }

.plans-list { display: flex; flex-direction: column; gap: 20px; scroll-margin-top: 80px; }
.plan-detail-card {
  padding: 32px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
}
.plan-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 20px;
}
.plan-detail-name { font-size: 1.3rem; font-weight: 700; margin-bottom: 4px; }
.plan-detail-summary { font-size: 0.85rem; color: var(--color-text-secondary); }
.plan-detail-price { text-align: right; flex-shrink: 0; }
.price-label { display: block; font-size: 0.78rem; color: var(--color-text-muted); }
.price-value { display: block; font-size: 1.3rem; font-weight: 700; color: var(--color-primary); margin: 4px 0 12px; }
.plan-use-btn {
  display: inline-block;
  padding: 8px 20px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  text-decoration: none;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s;
}
.plan-use-btn:hover { background: var(--color-primary-hover); }

.plan-specs-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px 24px;
  margin-bottom: 16px;
}
.spec-row { display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px solid rgba(0,0,0,0.04); font-size: 0.85rem; }
.spec-label { color: var(--color-text-muted); }
.spec-value { font-weight: 500; }

.plan-performance {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid rgba(0,0,0,0.06);
}
.perf-item { display: flex; gap: 8px; align-items: center; font-size: 0.85rem; }
.perf-label { color: var(--color-text-muted); }
.perf-stars { font-weight: 600; }

@media (max-width: 640px) {
  .plan-detail-header { flex-direction: column; }
  .plan-detail-price { text-align: left; }
  .plan-specs-detail { grid-template-columns: 1fr; }
}
</style>
