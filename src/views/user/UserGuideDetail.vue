<template>
  <div class="guide-detail" v-if="article">
    <div class="guide-back">
      <router-link to="/guides">
        <el-icon><ArrowLeft /></el-icon> 返回指南列表
      </router-link>
    </div>

    <article class="guide-article">
      <header class="guide-header">
        <div class="header-meta">
          <el-tag size="small" effect="plain">{{ article.category }}</el-tag>
          <span><el-icon><Clock /></el-icon> {{ article.readTime }} 分钟阅读</span>
          <span><el-icon><Calendar /></el-icon> {{ article.date }}</span>
        </div>
        <h1>{{ article.title }}</h1>
        <p class="header-summary">{{ article.summary }}</p>
        <div class="header-tags">
          <el-tag v-for="tag in article.tags" :key="tag" size="small" round>{{ tag }}</el-tag>
        </div>
      </header>

      <div class="guide-content markdown-body" v-html="renderedContent"></div>
    </article>
  </div>
  <div v-else class="not-found">
    <el-result icon="warning" title="文章未找到">
      <template #extra>
        <router-link to="/guides"><el-button type="primary">返回指南列表</el-button></router-link>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { useRoute } from "vue-router"
import { knowledgeArticles } from "@/api/knowledge.js"

const route = useRoute()
const article = computed(() => knowledgeArticles.find((a) => a.id === Number(route.params.id)))

function renderMarkdown(text) {
  if (!text) return ""
  let html = text
    .replace(/^### (.+)$/gm, "<h3>$1</h3>")
    .replace(/^## (.+)$/gm, "<h2>$1</h2>")
    .replace(/^\|(.+)\|$/gm, (match) => {
      if (match.includes("---")) return '<tr class="table-divider"></tr>'
      const cells = match.split("|").filter((c) => c.trim())
      const tag = match.includes("---") ? "th" : "td"
      return `<tr>${cells.map((c) => `<${tag}>${c.trim()}</${tag}>`).join("")}</tr>`
    })
    .replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>")
    .replace(/`([^`]+)`/g, "<code>$1</code>")
    .replace(/^- (.+)$/gm, "<li>$1</li>")

  const hasList = /<li>/.test(html)
  html = html.replace(/^(?!<[hl]|<\/?tr|<\/?t[dh]|<\/?li|<table)(.+)$/gm, "<p>$1</p>")
  if (hasList) html = html.replace(/(<li>[\s\S]*?<\/li>)/g, (m) => `<ul>${m}</ul>`)
  html = html.replace(/<p><\/p>/g, "")
  html = html.replace(/<p>\s*<tr/g, "<tr").replace(/<\/tr>\s*<\/p>/g, "</tr>")
  html = html.replace(/(<tr>[\s\S]*?<\/tr>)/g, (match) => {
    if (match.includes("table-divider")) return match
    if (!match.startsWith("<table")) return `<table>${match}</table>`
    return match
  })
  return html
}

const renderedContent = computed(() => renderMarkdown(article.value?.content || ""))
</script>

<style scoped>
.guide-detail { padding: 32px 24px; max-width: 800px; margin: 0 auto; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.guide-back { margin-bottom: 20px; }
.guide-back a { display: inline-flex; align-items: center; gap: 4px; color: var(--color-text-secondary); text-decoration: none; font-size: 0.9rem; }
.guide-back a:hover { color: var(--color-primary); }
.guide-article {
  background: var(--color-canvas);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
}
.guide-header {
  padding: 32px;
  border-bottom: 1px solid var(--color-border);
}
.header-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
.header-meta span { display: flex; align-items: center; gap: 4px; }
.guide-header h1 { font-size: 1.6rem; font-weight: 700; line-height: 1.25; margin-bottom: 8px; }
.header-summary { color: var(--color-text-secondary); font-size: 0.95rem; line-height: 1.5; }
.header-tags { display: flex; gap: 6px; margin-top: 12px; }
.guide-content { padding: 32px; }
.guide-content :deep(h2) { font-size: 1.2rem; font-weight: 600; margin: 24px 0 10px; padding-bottom: 6px; border-bottom: 1px solid var(--color-border-soft); }
.guide-content :deep(h3) { font-size: 1.05rem; font-weight: 600; margin: 16px 0 8px; }
.guide-content :deep(p) { margin: 8px 0; line-height: 1.7; color: var(--color-text-secondary); }
.guide-content :deep(strong) { color: var(--color-text-primary); }
.guide-content :deep(code) { background: var(--color-surface); padding: 2px 6px; border-radius: 4px; font-family: var(--font-mono); font-size: 0.85rem; }
.guide-content :deep(table) { width: 100%; border-collapse: collapse; margin: 12px 0; font-size: 0.9rem; }
.guide-content :deep(th) { background: var(--color-surface); padding: 8px 12px; text-align: left; font-weight: 600; border: 1px solid var(--color-border); }
.guide-content :deep(td) { padding: 8px 12px; border: 1px solid var(--color-border); }
.guide-content :deep(ul) { margin: 8px 0; padding-left: 20px; }
.guide-content :deep(li) { margin: 4px 0; line-height: 1.6; color: var(--color-text-secondary); }
.not-found { display: flex; justify-content: center; padding: 60px; }
</style>
