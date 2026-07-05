<template>
  <div class="user-guides">
    <div class="page-header">
      <h1>选购指南</h1>
      <p>从入门到精通，帮你做出明智的选购决策</p>
    </div>

    <div class="filters">
      <el-radio-group v-model="activeCategory" size="small">
        <el-radio-button value="全部">全部</el-radio-button>
        <el-radio-button value="选购指南">选购指南</el-radio-button>
        <el-radio-button value="技术科普">技术科普</el-radio-button>
        <el-radio-button value="装机教程">装机教程</el-radio-button>
      </el-radio-group>
    </div>

    <div class="guides-grid">
      <div
        v-for="article in filteredArticles"
        :key="article.id"
        class="guide-card"
        @click="$router.push(`/guides/${article.id}`)"
      >
        <div class="guide-meta">
          <el-tag size="small" effect="plain">{{ article.category }}</el-tag>
          <span class="guide-time">{{ article.readTime }} 分钟</span>
        </div>
        <h3 class="guide-title">{{ article.title }}</h3>
        <p class="guide-summary">{{ article.summary }}</p>
        <div class="guide-tags">
          <el-tag v-for="tag in article.tags" :key="tag" size="small" effect="plain" round>{{ tag }}</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"
import { knowledgeArticles, categories } from "@/api/knowledge.js"

const activeCategory = ref("全部")

const filteredArticles = computed(() => {
  if (activeCategory.value === "全部") return knowledgeArticles
  return knowledgeArticles.filter((a) => a.category === activeCategory.value)
})
</script>

<style scoped>
.user-guides { padding: 32px 24px; max-width: 1000px; margin: 0 auto; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { color: var(--color-text-muted); margin-top: 4px; }

.filters { margin-bottom: 24px; }

.guides-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }

.guide-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.guide-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
  border-color: var(--color-primary);
}
.guide-meta { display: flex; align-items: center; gap: 8px; }
.guide-time { font-size: 0.78rem; color: var(--color-text-muted); }
.guide-title { font-size: 1.05rem; font-weight: 600; line-height: 1.35; }
.guide-summary { font-size: 0.85rem; color: var(--color-text-secondary); line-height: 1.5; flex: 1; }
.guide-tags { display: flex; gap: 4px; flex-wrap: wrap; }

@media (max-width: 640px) {
  .guides-grid { grid-template-columns: 1fr; }
}
</style>
