<template>
  <div class="user-home">
    <!-- Stripe 风格 Hero -->
    <section class="hero">
      <div class="hero-mesh">
        <div class="mesh-blob blob-1"></div>
        <div class="mesh-blob blob-2"></div>
        <div class="mesh-blob blob-3"></div>
      </div>
      <div class="hero-content">
        <span class="hero-eyebrow">PC DIY 智能装机平台</span>
        <h1 class="hero-title">
          组装你的<span class="hero-accent">理想电脑</span>
        </h1>
        <p class="hero-subtitle">智能兼容性检测 · 实时价格计算 · 专业组装服务</p>
        <div class="hero-actions">
          <router-link to="/build" class="hero-cta-primary">
            开始装机 <el-icon><ArrowRight /></el-icon>
          </router-link>
          <router-link to="/plans" class="hero-cta-secondary">
            查看推荐配置
          </router-link>
        </div>
        <div class="hero-stats">
          <div class="hero-stat">
            <span class="hero-stat-num">8</span>
            <span class="hero-stat-label">大类配件</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-num">50+</span>
            <span class="hero-stat-label">精选型号</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-num">7</span>
            <span class="hero-stat-label">兼容规则</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-num">100%</span>
            <span class="hero-stat-label">自主搭配</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 推荐配置 - Notion 彩色卡片 -->
    <section class="section">
      <div class="section-inner">
        <div class="section-header-center">
          <span class="section-eyebrow">热门方案</span>
          <h2>精选推荐配置</h2>
          <p>从入门到发烧，总有一套适合你</p>
        </div>
        <div class="plans-grid">
          <div
            class="plan-card"
            v-for="plan in recommendedBuilds"
            :key="plan.id"
            :style="{ background: plan.tint }"
            @click="$router.push('/plans#' + plan.id)"
          >
            <div class="plan-badge" v-if="plan.badge">{{ plan.badge }}</div>
            <div class="plan-name">{{ plan.name }}</div>
            <div class="plan-tag">{{ plan.tag }}</div>
            <div class="plan-summary">{{ plan.summary }}</div>
            <div class="plan-specs">
              <div class="plan-spec" v-for="spec in plan.specs.slice(0, 4)" :key="spec.label">
                <span class="plan-spec-label">{{ spec.label }}</span>
                <span class="plan-spec-value">{{ spec.value }}</span>
              </div>
            </div>
            <div class="plan-footer">
              <span class="plan-price">¥{{ plan.price.toLocaleString() }}</span>
              <span class="plan-link">查看详情 <el-icon><ArrowRight /></el-icon></span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 如何工作 - 流程步骤 -->
    <section class="section section-alt">
      <div class="section-inner">
        <div class="section-header-center">
          <span class="section-eyebrow">使用流程</span>
          <h2>四步拥有理想电脑</h2>
        </div>
        <div class="steps-grid">
          <div class="step-card" v-for="(step, i) in buildSteps" :key="i">
            <div class="step-number">{{ i + 1 }}</div>
            <el-icon :size="32" color="var(--color-primary)"><component :is="step.icon" /></el-icon>
            <h3>{{ step.title }}</h3>
            <p>{{ step.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 为什么选择我们 -->
    <section class="section">
      <div class="section-inner">
        <div class="section-header-center">
          <span class="section-eyebrow">为什么选择我们</span>
          <h2>专业 · 智能 · 省心</h2>
        </div>
        <div class="features-grid">
          <div class="feature-card" v-for="feat in features" :key="feat.title">
            <div class="feature-icon" :style="{ background: feat.bg }">
              <el-icon :size="24" :color="feat.color"><component :is="feat.icon" /></el-icon>
            </div>
            <h3>{{ feat.title }}</h3>
            <p>{{ feat.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Banner -->
    <section class="cta-banner">
      <div class="section-inner cta-inner">
        <h2>准备好组装你的电脑了吗？</h2>
        <p>立即开始，免费使用，无任何隐藏费用</p>
        <router-link to="/build" class="cta-button-large">
          开始免费装机 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { recommendedBuilds, buildSteps } from "@/api/user.js"

const features = [
  { icon: "Cpu", title: "海量配件库", desc: "覆盖 Intel/AMD/NVIDIA 等主流品牌的精选型号", bg: "var(--color-tint-lavender)", color: "var(--color-primary)" },
  { icon: "CircleCheck", title: "智能兼容检测", desc: "7 项兼容规则实时校验，杜绝不匹配问题", bg: "var(--color-tint-mint)", color: "var(--color-success)" },
  { icon: "Coin", title: "实时价格计算", desc: "每选一个配件，总价即时更新，预算精准可控", bg: "var(--color-tint-sky)", color: "var(--color-info)" },
  { icon: "Truck", title: "专业装机服务", desc: "下单后专业工程师组装测试，整机调试后发货", bg: "var(--color-tint-peach)", color: "var(--color-warning)" },
]
</script>

<style scoped>
.user-home { overflow-x: hidden; }

/* Hero - Stripe mesh gradient */
.hero {
  position: relative;
  padding: 80px 24px 60px;
  background: linear-gradient(135deg, #fef7d6 0%, #ffe8d4 20%, #fde0ec 40%, #e6e0f5 60%, #dcecfa 80%, #d9f3e1 100%);
  overflow: hidden;
  text-align: center;
}
.hero-mesh { position: absolute; inset: 0; pointer-events: none; opacity: 0.4; }
.mesh-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
}
.blob-1 { width: 400px; height: 400px; background: #dcecfa; top: -100px; left: -100px; }
.blob-2 { width: 300px; height: 300px; background: #fde0ec; bottom: -80px; right: -80px; }
.blob-3 { width: 250px; height: 250px; background: #e6e0f5; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.hero-content { position: relative; z-index: 1; max-width: 800px; margin: 0 auto; }
.hero-eyebrow {
  display: inline-block;
  padding: 4px 14px;
  border-radius: var(--radius-full);
  background: rgba(94,106,210,0.1);
  color: var(--color-primary);
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 16px;
}
.hero-title {
  font-size: 3.2rem;
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.1;
  color: var(--color-text-primary);
  margin-bottom: 16px;
}
.hero-accent { color: var(--color-primary); }
.hero-subtitle {
  font-size: 1.1rem;
  color: var(--color-text-secondary);
  margin-bottom: 28px;
  line-height: 1.5;
}
.hero-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 40px;
}
.hero-cta-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 28px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s;
}
.hero-cta-primary:hover { background: var(--color-primary-hover); transform: translateY(-2px); box-shadow: var(--shadow-md); }
.hero-cta-secondary {
  display: inline-flex;
  align-items: center;
  padding: 12px 28px;
  border-radius: var(--radius-md);
  background: var(--color-canvas);
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  border: 1px solid var(--color-border);
  transition: all 0.2s;
}
.hero-cta-secondary:hover { border-color: var(--color-primary); color: var(--color-primary); }
.hero-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
}
.hero-stat { display: flex; flex-direction: column; gap: 2px; }
.hero-stat-num { font-size: 1.5rem; font-weight: 700; color: var(--color-text-primary); }
.hero-stat-label { font-size: 0.8rem; color: var(--color-text-muted); }

/* Sections */
.section { padding: 80px 0; }
.section-alt { background: var(--color-surface); }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.section-header-center {
  text-align: center;
  margin-bottom: 48px;
}
.section-eyebrow {
  display: inline-block;
  padding: 4px 14px;
  border-radius: var(--radius-full);
  background: var(--color-primary-soft);
  color: var(--color-primary);
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 12px;
}
.section-header-center h2 {
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}
.section-header-center p { color: var(--color-text-muted); font-size: 1rem; }

/* Plans Grid - Notion tinted cards */
.plans-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.plan-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.25s ease;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  border: 1px solid transparent;
}
.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--color-border);
}
.plan-badge {
  position: absolute;
  top: -8px;
  right: 12px;
  padding: 2px 10px;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: #fff;
  font-size: 0.72rem;
  font-weight: 600;
}
.plan-name { font-size: 1.15rem; font-weight: 700; }
.plan-tag {
  font-size: 0.8rem;
  color: var(--color-text-muted);
  font-weight: 500;
}
.plan-summary { font-size: 0.82rem; color: var(--color-text-secondary); line-height: 1.5; flex: 1; }
.plan-specs { display: flex; flex-direction: column; gap: 3px; margin-top: 8px; }
.plan-spec { display: flex; justify-content: space-between; font-size: 0.75rem; }
.plan-spec-label { color: var(--color-text-muted); }
.plan-spec-value { font-weight: 500; color: var(--color-text-primary); text-align: right; }
.plan-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0,0,0,0.06);
}
.plan-price { font-size: 1.15rem; font-weight: 700; color: var(--color-primary); }
.plan-link { font-size: 0.82rem; color: var(--color-primary); display: flex; align-items: center; gap: 4px; }

/* Steps */
.steps-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.step-card {
  text-align: center;
  padding: 32px 20px;
  background: var(--color-canvas);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  position: relative;
  transition: all 0.2s;
}
.step-card:hover { box-shadow: var(--shadow-md); }
.step-number {
  position: absolute;
  top: -10px;
  left: 20px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  font-size: 0.8rem;
  font-weight: 700;
}
.step-card h3 { font-size: 1rem; font-weight: 600; margin: 12px 0 6px; }
.step-card p { font-size: 0.85rem; color: var(--color-text-muted); line-height: 1.5; }

/* Features */
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.feature-card {
  text-align: center;
  padding: 28px 16px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  transition: all 0.2s;
}
.feature-card:hover { box-shadow: var(--shadow-md); transform: translateY(-2px); }
.feature-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  margin: 0 auto 16px;
}
.feature-card h3 { font-size: 0.95rem; font-weight: 600; margin-bottom: 6px; }
.feature-card p { font-size: 0.82rem; color: var(--color-text-muted); line-height: 1.5; }

/* CTA Banner */
.cta-banner {
  padding: 80px 0;
  background: linear-gradient(135deg, var(--color-sidebar) 0%, #1a1d30 100%);
  text-align: center;
}
.cta-inner { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.cta-banner h2 { font-size: 2rem; font-weight: 700; color: var(--color-text-on-dark); }
.cta-banner p { color: var(--color-text-on-dark-muted); font-size: 1rem; }
.cta-button-large {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding: 14px 36px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: 1.05rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s;
}
.cta-button-large:hover { background: var(--color-primary-hover); transform: translateY(-2px); box-shadow: var(--shadow-lg); }

/* Responsive */
@media (max-width: 1024px) {
  .plans-grid, .features-grid, .steps-grid { grid-template-columns: repeat(2, 1fr); }
  .hero-title { font-size: 2.4rem; }
}
@media (max-width: 640px) {
  .plans-grid, .features-grid, .steps-grid { grid-template-columns: 1fr; }
  .hero-stats { flex-wrap: wrap; gap: 20px; }
  .hero-title { font-size: 1.8rem; }
  .hero-actions { flex-direction: column; align-items: center; }
}
</style>
