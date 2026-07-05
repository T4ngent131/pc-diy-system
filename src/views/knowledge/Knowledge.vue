<template>
  <div class="knowledge-page">
    <div class="section-header">
      <h2>知识库</h2>
      <div class="header-actions">
        <el-select v-model="activeCategory" size="small" style="width:130px">
          <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
        </el-select>
        <el-input v-model="searchText" placeholder="搜索文章..." clearable size="small" style="width:200px" prefix-icon="Search" />
      </div>
    </div>

    <!-- 文章网格 - Notion tinted 卡片风格 -->
    <div class="article-grid">
      <div
        v-for="article in filteredArticles"
        :key="article.id"
        class="article-card"
        :style="{ background: cardTints[article.id % cardTints.length] }"
        @click="$router.push(`/knowledge/${article.id}`)"
      >
        <div class="article-meta">
          <el-tag size="small" effect="plain">{{ article.category }}</el-tag>
          <span class="article-read-time">{{ article.readTime }} 分钟</span>
        </div>
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-summary">{{ article.summary }}</p>
        <div class="article-footer">
          <div class="article-author">
            <el-icon><User /></el-icon>
            <span>{{ article.author }}</span>
          </div>
          <span class="article-date">{{ article.date }}</span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredArticles.length === 0" class="empty-state">
      <el-icon :size="48" color="var(--color-text-muted)"><Reading /></el-icon>
      <p>暂无文章</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"
import { useRouter } from "vue-router"
import { knowledgeArticles, categories as catList } from "@/api/knowledge.js"

const router = useRouter()
const activeCategory = ref("全部")
const searchText = ref("")
const categories = ref(catList)
const cardTints = ["var(--color-tint-peach)", "var(--color-tint-rose)", "var(--color-tint-mint)", "var(--color-tint-lavender)", "var(--color-tint-sky)", "var(--color-tint-yellow)"]

const filteredArticles = computed(() => {
  let items = knowledgeArticles
  if (activeCategory.value !== "全部") {
    items = items.filter((a) => a.category === activeCategory.value)
  }
  if (searchText.value) {
    const q = searchText.value.toLowerCase()
    items = items.filter((a) => a.title.toLowerCase().includes(q) || a.summary.toLowerCase().includes(q))
  }
  return items
})
</script>

<style scoped>
.knowledge-page { animation: fadeIn 0.3s ease; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.header-actions {
  display: flex;
  gap: 8px;
}
.article-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.article-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  gap: 12px;
  border: 1px solid transparent;
}
.article-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
  border-color: var(--color-border);
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}
.article-read-time {
  font-size: 0.75rem;
  color: var(--color-text-muted);
}
.article-title {
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.35;
  color: var(--color-text-primary);
}
.article-summary {
  font-size: 0.85rem;
  color: var(--color-text-secondary);
  line-height: 1.5;
  flex: 1;
}
.article-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.78rem;
  color: var(--color-text-muted);
}
.article-author {
  display: flex;
  align-items: center;
  gap: 4px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px;
  color: var(--color-text-muted);
  gap: 12px;
}

@media (max-width: 1024px) {
  .article-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .article-grid { grid-template-columns: 1fr; }
}
</style>
