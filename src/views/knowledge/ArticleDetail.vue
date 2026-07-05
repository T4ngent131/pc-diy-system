<template>
  <div class="article-detail" v-if="article">
    <div class="article-back">
      <el-button text @click="$router.push('/knowledge')">
        <el-icon><ArrowLeft /></el-icon> 返回知识库
      </el-button>
    </div>

    <div class="article-content">
      <div class="article-header">
        <div class="article-meta">
          <el-tag size="small">{{ article.category }}</el-tag>
          <span class="meta-item"><el-icon><User /></el-icon> {{ article.author }}</span>
          <span class="meta-item"><el-icon><Clock /></el-icon> {{ article.readTime }} 分钟阅读</span>
          <span class="meta-item"><el-icon><Calendar /></el-icon> {{ article.date }}</span>
        </div>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-tags">
          <el-tag v-for="tag in article.tags" :key="tag" size="small" effect="plain">{{ tag }}</el-tag>
        </div>
      </div>

      <div class="article-body markdown-body" v-html="renderedContent"></div>
    </div>
  </div>
  <div v-else class="not-found">
    <el-result icon="warning" title="文章未找到" sub-title="请返回知识库浏览其他文章">
      <template #extra>
        <el-button type="primary" @click="$router.push('/knowledge')">返回知识库</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"
import { useRoute } from "vue-router"
import { knowledgeArticles } from "@/api/knowledge.js"

const route = useRoute()
const article = computed(() => knowledgeArticles.find((a) => a.id === Number(route.params.id)))

// 简易 Markdown 渲染（处理 h2/h3/表格/代码块/粗体/列表）
function renderMarkdown(text) {
  if (!text) return ""
  let html = text
    // Headers
    .replace(/^### (.+)$/gm, "<h3>$1</h3>")
    .replace(/^## (.+)$/gm, "<h2>$1</h2>")
    // Tables
    .replace(/^\|(.+)\|$/gm, (match) => {
      if (match.includes("---")) return '<tr class="table-divider"></tr>'
      const cells = match.split("|").filter((c) => c.trim())
      const tag = match.includes("---") ? "th" : "td"
      return `<tr>${cells.map((c) => `<${tag}>${c.trim()}</${tag}>`).join("")}</tr>`
    })
    // Bold
    .replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>")
    // Inline code
    .replace(/`([^`]+)`/g, "<code>$1</code>")
    // Unordered list
    .replace(/^- (.+)$/gm, "<li>$1</li>")
    // Paragraphs
    .replace(/^(?!<[hl]|<tr|<\/tr|<li)(.+)$/gm, "<p>$1</p>")
    // Cleanup
    .replace(/(<li>.*<\/li>)/s, (m) => `<ul>${m}</ul>`)
    .replace(/<p><\/p>/g, "")
    .replace(/<p>\s*<tr/g, "<tr")
    .replace(/<\/tr>\s*<\/p>/g, "</tr>")

  // Wrap tables
  html = html.replace(/(<tr>.*?<\/tr>)/gs, (match) => {
    if (match.includes("table-divider")) return match
    if (!match.startsWith("<table")) {
      return `<table>${match}</table>`
    }
    return match
  })

  return html
}

const renderedContent = computed(() => renderMarkdown(article.value?.content || ""))
</script>

<style scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  animation: fadeIn 0.3s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.article-back { margin-bottom: 16px; }
.article-content {
  background: var(--color-canvas);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 32px;
}
.article-header {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-border);
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 0.85rem;
  color: var(--color-text-muted);
  flex-wrap: wrap;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.article-title {
  font-size: 1.6rem;
  font-weight: 700;
  line-height: 1.25;
  margin-bottom: 12px;
  letter-spacing: -0.02em;
}
.article-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.article-body :deep(h2) {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 20px 0 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--color-border-soft);
}
.article-body :deep(h3) {
  font-size: 1.05rem;
  font-weight: 600;
  margin: 16px 0 8px;
}
.article-body :deep(p) {
  margin: 8px 0;
  line-height: 1.7;
  color: var(--color-text-secondary);
}
.article-body :deep(strong) {
  color: var(--color-text-primary);
}
.article-body :deep(code) {
  background: var(--color-surface);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 0.85rem;
}
.article-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  font-size: 0.9rem;
}
.article-body :deep(th) {
  background: var(--color-surface);
  padding: 8px 12px;
  text-align: left;
  font-weight: 600;
  border: 1px solid var(--color-border);
}
.article-body :deep(td) {
  padding: 8px 12px;
  border: 1px solid var(--color-border);
}
.article-body :deep(ul) {
  margin: 8px 0;
  padding-left: 20px;
}
.article-body :deep(li) {
  margin: 4px 0;
  line-height: 1.6;
  color: var(--color-text-secondary);
}
.not-found {
  display: flex;
  justify-content: center;
  padding: 60px;
}
</style>
