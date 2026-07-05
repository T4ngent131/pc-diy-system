<template>
  <div class="compatibility" v-if="compatibility">
    <!-- 状态图标 -->
    <div class="status-banner" :class="{ success: compatibility.compatible, warning: !compatibility.compatible && compatibility.warnings.length > 0, error: !compatibility.compatible }">
      <el-icon :size="20">
        <CircleCheck v-if="compatibility.compatible && compatibility.warnings.length === 0" />
        <WarningFilled v-else-if="compatibility.compatible" />
        <CircleCloseFilled v-else />
      </el-icon>
      <span class="status-text">
        {{ compatibility.compatible ? (compatibility.warnings.length > 0 ? '配置兼容，但有警告' : '所有配件兼容性良好') : '存在兼容性问题' }}
      </span>
    </div>

    <!-- 问题列表 -->
    <div v-if="compatibility.issues.length > 0" class="issue-list">
      <div v-for="(issue, i) in compatibility.issues" :key="i" class="issue-item error">
        <el-icon><Close /></el-icon>
        <span>{{ issue }}</span>
      </div>
    </div>
    <div v-if="compatibility.warnings.length > 0" class="issue-list">
      <div v-for="(warn, i) in compatibility.warnings" :key="i" class="issue-item warning">
        <el-icon><Warning /></el-icon>
        <span>{{ warn }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  compatibility: { type: Object, default: () => ({ compatible: true, issues: [], warnings: [] }) },
})
</script>

<style scoped>
.compatibility {
  margin-bottom: 16px;
}
.status-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  font-weight: 500;
  font-size: 0.9rem;
}
.status-banner.success {
  background: var(--color-success-soft);
  color: var(--color-success);
}
.status-banner.warning {
  background: var(--color-warning-soft);
  color: var(--color-warning);
}
.status-banner.error {
  background: var(--color-danger-soft);
  color: var(--color-danger);
}
.issue-list {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.issue-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  font-size: 0.85rem;
}
.issue-item.error {
  background: var(--color-danger-soft);
  color: var(--color-danger);
}
.issue-item.warning {
  background: var(--color-warning-soft);
  color: var(--color-warning);
}
</style>
